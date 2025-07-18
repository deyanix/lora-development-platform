#!/bin/bash

# This script automates PlatformIO build and upload processes.
# It can discover compatible devices if no ports are specified,
# builds the project, and then uploads to all specified (or discovered)
# ports concurrently using background jobs.

# --- Configuration ---
# The VID:PID of the device to look for if no ports are specified.
# This corresponds to "VID:PID=10C4:EA60" in the original PowerShell script.
TARGET_HWID="VID:PID=10C4:EA60"

# --- Function Definitions ---

# pio_message: A custom function to display colored messages to the console,
# mimicking PowerShell's Write-Host with foreground colors and a timestamped prefix.
# Arguments:
#   $1: The message string to display.
#   $2: The desired content color (e.g., "yellow", "cyan", "red", "green", "blue").
#   $3: (Optional) The port name, if the message is specific to a port.
pio_message() {
    local message="$1"
    local content_color="$2" # e.g., "yellow", "cyan", "red", "green", "blue"
    local port="$3"

    # ANSI escape codes for colors
    local prefix_ansi_color="\033[0;37m" # Gray for the prefix
    local content_ansi_color=""
    local reset_ansi_color="\033[0m"    # Reset to default color

    # Determine the ANSI color code based on the content_color argument
    case "$content_color" in
        "yellow") content_ansi_color="\033[0;33m" ;;
        "cyan") content_ansi_color="\033[0;36m" ;;
        "red") content_ansi_color="\033[0;31m" ;;
        "green") content_ansi_color="\033[0;32m" ;;
        "blue") content_ansi_color="\033[0;34m" ;;
        *) content_ansi_color="$reset_ansi_color" ;; # Default to no color
    esac

    # Get current timestamp with milliseconds
    local timestamp=$(date +"%Y-%m-%d %H:%M:%S.%3N")
    local prefix=""

    # Construct the prefix based on whether a port is provided
    if [[ -n "$port" ]]; then
        prefix="$timestamp [PIO|$port]"
    else
        prefix="$timestamp [PIO]"
    fi

    # Print the formatted message with colors
    echo -e "${prefix_ansi_color}${prefix}${reset_ansi_color} ${content_ansi_color}${message}${reset_ansi_color}"
}

# --- Main Script Logic ---

# Initialize an array to hold the ports.
# This array will be populated either from command-line arguments or device discovery.
ports=()

# Parse command-line arguments for ports.
# If arguments are provided, they are treated as port names.
# Example usage: ./script.sh /dev/ttyUSB0 /dev/ttyUSB1
if [[ -n "$1" ]]; then
    for arg in "$@"; do
        ports+=("$arg")
    done
fi

# Check if the 'ports' array is empty.
# If no ports were provided as command-line arguments, attempt device discovery.
if [[ ${#ports[@]} -eq 0 ]]; then
    pio_message "No ports specified. Discovering devices..." "yellow"

    # Execute 'pio device list --json-output' to get device information in JSON format.
    # Redirect stderr to /dev/null to suppress potential error messages from pio if no devices are found.
    json_output=$(pio device list --json-output 2>/dev/null)

    # Check if any JSON output was received.
    if [[ -n "$json_output" ]]; then
        # Ensure 'jq' is installed, as it's required for JSON parsing.
        if ! command -v jq &> /dev/null; then
            pio_message "Error: 'jq' is not installed. Please install 'jq' to parse JSON output (e.g., 'sudo apt-get install jq' or 'brew install jq')." "red"
            exit 1
        fi

        # Use 'jq' to filter the JSON output:
        # .[]: Iterate over each object in the root array.
        # select(.hwid | contains("$TARGET_HWID")): Filter objects where 'hwid' contains the target VID:PID.
        # .port: Extract the 'port' field from the filtered objects.
        # -r: Output raw strings (without quotes).
        filtered_ports=$(echo "$json_output" | jq -r ".[] | select(.hwid | contains(\"$TARGET_HWID\")) | .port" 2>/dev/null)

        # Check if any compatible ports were found.
        if [[ -n "$filtered_ports" ]]; then
            # Read each line from 'filtered_ports' (which are discovered port names)
            # into the 'ports' array. IFS= read -r ensures correct handling of whitespace.
            while IFS= read -r line; do
                ports+=("$line")
            done <<< "$filtered_ports"
            pio_message "Discovered ports: $(IFS=', '; echo "${ports[*]}")" "cyan"
        else
            pio_message "No compatible devices found with $TARGET_HWID." "red"
            exit 1
        fi
    else
        pio_message "Failed to get device list from 'pio device list --json-output'. Is PlatformIO CLI installed and in your PATH?" "red"
        exit 1
    fi
fi

# Display the ports that will be used for upload.
pio_message "Used ports: $(IFS=', '; echo "${ports[*]}")" "cyan"

# --- Build Process ---

pio_message "Starting build..." "blue"

# Execute 'pio run -e main' to build the project.
# Redirect both stdout and stderr to /dev/null to keep the console clean during build.
pio run -e main > /dev/null 2>&1
build_exit_code=$? # Capture the exit code of the last command.

# Check the build's exit code. If non-zero, the build failed.
if [[ "$build_exit_code" -ne 0 ]]; then
    pio_message "Build failed! (Exit Code: ${build_exit_code}). Aborting upload." "red"
    exit 1
fi

pio_message "Build successful!" "green"

# --- Concurrent Upload Jobs ---

# Declare associative arrays to keep track of background job PIDs and their
# corresponding temporary result files.
declare -A job_pids        # Maps PID to port name (e.g., [12345]="/dev/ttyUSB0")
declare -A job_temp_files  # Maps PID to its temporary result file path

# Store the current working directory. This is important because background
# subshells might change their directory, and we want them to run pio from
# the original project root.
current_working_directory=$(pwd)

# Create a temporary directory to store the results of each upload job.
# The 'mktemp -d' command creates a unique temporary directory.
temp_dir=$(mktemp -d -t pio_upload_results_XXXXXX)
if [[ ! -d "$temp_dir" ]]; then
    pio_message "Error: Failed to create temporary directory for job results." "red"
    exit 1
fi

# Set a trap to ensure the temporary directory is removed when the script exits,
# regardless of whether it exits normally or due to an error.
trap "rm -rf '$temp_dir'" EXIT

# Loop through each port to start an upload job in the background.
for p in "${ports[@]}"; do
    pio_message "Starting upload job for port..." "blue" "$p"

    # Create a unique temporary file for this specific job's result.
    # Replace '/' in port names with '_' to make them valid filename components.
    job_result_file="${temp_dir}/result_${p//\//_}.txt"

    # Run the upload command in a subshell in the background.
    # The subshell:
    # 1. Changes to the original working directory.
    # 2. Executes the 'pio run' command for upload (nobuild means it won't rebuild).
    # 3. Redirects its stdout and stderr to /dev/null.
    # 4. Captures the exit code of the 'pio run' command.
    # 5. Writes the port name and exit code to its dedicated temporary result file.
    (
        cd "$current_working_directory" || exit 1 # Change to original directory, exit if fails
        pio run -t nobuild -t upload -e main --upload-port "$p" > /dev/null 2>&1
        exit_code=$?
        echo "Port=$p" > "$job_result_file"
        echo "ExitCode=$exit_code" >> "$job_result_file"
    ) &
    job_pid=$! # Capture the PID (Process ID) of the background subshell.

    # Store the PID, port, and result file path in the associative arrays.
    job_pids["$job_pid"]="$p"
    job_temp_files["$job_pid"]="$job_result_file"
done

pio_message "Waiting for all upload jobs to complete and displaying results as they finish..." "blue"

# Loop while there are still active background jobs being tracked.
while [[ ${#job_pids[@]} -gt 0 ]]; do
    # 'wait -n' waits for any single child process to terminate.
    # This feature requires Bash 4.3 or newer. If you are on an older Bash version,
    # you might need a more complex polling mechanism (e.g., checking 'kill -0 $pid').
    wait -n

    # Iterate through the PIDs of the jobs we are tracking.
    for pid in "${!job_pids[@]}"; do
        # Check if the process with this PID is still running.
        # 'kill -0 $pid' sends no signal but checks if the process exists.
        # If it returns non-zero, the process has exited.
        if ! kill -0 "$pid" &> /dev/null; then
            # The process has exited. Retrieve its information.
            completed_port="${job_pids["$pid"]}"
            result_file="${job_temp_files["$pid"]}"

            # Check if the temporary result file exists.
            if [[ -f "$result_file" ]]; then
                # Read the port and exit code from the temporary file.
                result_port=$(grep "Port=" "$result_file" | cut -d'=' -f2)
                result_exit_code=$(grep "ExitCode=" "$result_file" | cut -d'=' -f2)

                # Display the upload result message based on the exit code.
                if [[ "$result_exit_code" -eq 0 ]]; then
                    pio_message "Upload to port successful!" "green" "$result_port"
                else
                    pio_message "Upload to port failed! (Exit Code: ${result_exit_code})" "red" "$result_port"
                fi
                rm "$result_file" # Clean up the temporary result file.
            else
                pio_message "Job completed, but no result file was found for port." "yellow" "$completed_port"
            fi

            # Remove the completed job from our tracking arrays.
            unset job_pids["$pid"]
            unset job_temp_files["$pid"]
        fi
    done
    sleep 0.1 # Small delay to prevent busy-waiting while checking job status.
done

pio_message "All upload processes completed." "blue"