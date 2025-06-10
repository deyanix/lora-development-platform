import serial
import serial.tools.list_ports
import time
import threading
import curses
from datetime import datetime
import argparse

BAUD_RATE = 115200
VENDOR_ID = '10C4'
PRODUCT_ID = 'EA60'

# Define color pairs
COLOR_RED_ON_BLACK = 1
COLOR_GREEN_ON_BLACK = 2
COLOR_WHITE_ON_BLACK = 3 # For general text
COLOR_CYAN_ON_BLACK = 4 # For port number on top
COLOR_YELLOW_ON_BLACK = 5 # For status panel text

# Shared data structure for message counts, protected by a lock
# Key: port_path, Value: {'tx': 0, 'rx': 0}
message_counts = {}
counts_lock = threading.Lock()

def find_serial_ports(vendor_id, product_id):
    found_ports = []
    for port in serial.tools.list_ports.comports():
        if port.vid == int(vendor_id, 16) and port.pid == int(product_id, 16):
            found_ports.append(port.device)
    return found_ports

def read_from_port(port, win):
    height, width = win.getmaxyx()
    inner_width = width - 2 # Account for left and right borders

    # Move cursor to the initial writing position for data:
    # Row 1, Column 1 (inside the box's content area)
    win.move(1, 1)
    win.refresh()

    while True:
        if port.is_open:
            try:
                line = port.readline().decode('utf-8', errors="ignore").strip()
                if line:
                    timestamp = datetime.now().strftime("%H:%M:%S")

                    # Check for TX or RX in the raw line (case-insensitive)
                    is_tx = "tx=" in line.lower() or "=tx" in line.lower()
                    is_rx = "rx=" in line.lower() or "=rx" in line.lower()

                    # Update message counts
                    with counts_lock:
                        if port.port not in message_counts:
                            message_counts[port.port] = {'tx': 0, 'rx': 0}
                        if "tx=" in line.lower():
                            message_counts[port.port]['tx'] += 1
                        if "rx=" in line.lower():
                            message_counts[port.port]['rx'] += 1

                    # Construct the full display line with timestamp
                    display_content = f"{timestamp} | {line}"
                    display_line_to_print = display_content[:inner_width] # Truncate

                    color_attr = curses.A_NORMAL # Default
                    if is_tx:
                        color_attr = curses.color_pair(COLOR_RED_ON_BLACK)
                    elif is_rx:
                        color_attr = curses.color_pair(COLOR_GREEN_ON_BLACK)
                    else:
                        color_attr = curses.color_pair(COLOR_WHITE_ON_BLACK)

                    current_y, current_x = win.getyx()
                    if current_x != 1: # Cursor is at x=0 after newline, move to x=1
                        win.move(current_y, 1)

                    win.clrtoeol() # Clear from current cursor position to end of line
                    win.addstr(display_line_to_print, color_attr)
                    win.addstr("\n") # Move cursor to next line, column 0, potentially scrolling

                    win.box()
                    win.refresh()

            except serial.SerialException as e:
                error_message = f"Serial Error: {e}"[:inner_width].ljust(inner_width)
                current_y, current_x = win.getyx()
                win.addstr(height - 2, 1, error_message, curses.color_pair(COLOR_RED_ON_BLACK))
                win.refresh()
                win.move(current_y, current_x)
                break
            except Exception as e:
                error_message = f"General Error: {e}"[:inner_width].ljust(inner_width)
                current_y, current_x = win.getyx()
                win.addstr(height - 2, 1, error_message, curses.color_pair(COLOR_RED_ON_BLACK))
                win.refresh()
                win.move(current_y, current_x)
        else:
            break

def initialize_port(port, idx, args, freq_map):
    commands = [
        '+ID?\n',
        f'+FRQ={freq_map.get(idx, "868000000")}\n',
        '+BW=3\n',
        '+SF=12\n',
        '+PWR=12\n',
        '+CRT=2\n',
        '+PRLEN=8\n',
        '+PYLEN=0\n',
        '+CRC=0\n',
        '+IIQ=1\n',
        '+STO=0\n',
        '+TXTO=3000\n',
        f'+MODE={"RX" if idx == 0 else "TX"}\n',
        f'+INTERVAL={args.interval}\n',
        f'+MINDELTA={args.mindelta}\n',
        f'+MAXDELTA={args.maxdelta}\n',
        '+AUTO=RANDOM\n',
        '+FRQ?\n',
        '+BW?\n',
        '+SF?\n',
        '+PWR?\n',
        '+CRT?\n',
        '+PRLEN?\n',
        '+PYLEN?\n',
        '+CRC?\n',
        '+IIQ?\n',
        '+STO?\n',
        '+TXTO?\n',
        '+MODE?\n',
        '+PUSH\n',
        '+TOA=4?\n',
        '+AUTO?\n',
        '+INTERVAL?\n',
        '+MINDELTA?\n',
        '+MAXDELTA?\n'
    ]
    for cmd in commands:
        port.write(cmd.encode('utf-8'))
        time.sleep(0.1)

# ---
## Status Panel Update Function
def update_status_panel(stdscr, status_win, port_paths):
    status_height, status_width = status_win.getmaxyx()

    # Clear the status window
    for i in range(1, status_height - 1):
        status_win.addstr(i, 1, ' ' * (status_width - 2))

    status_win.box() # Redraw the border
    status_win.addstr(0, (status_width - len(" Message Counts ")) // 2, " Message Counts ", curses.A_BOLD | curses.color_pair(COLOR_YELLOW_ON_BLACK))

    y_offset = 1
    with counts_lock:
        for path in port_paths:
            if path in message_counts:
                tx_count = message_counts[path]['tx']
                rx_count = message_counts[path]['rx']

                display_text = f"{path}: TX={tx_count:<5} RX={rx_count:<5}"
                # Ensure text fits within the status panel's inner width
                display_text = display_text[:status_width - 2]

                if y_offset < status_height - 1: # Stay within inner bounds
                    status_win.addstr(y_offset, 1, display_text, curses.color_pair(COLOR_YELLOW_ON_BLACK))
                    y_offset += 1

            if y_offset >= status_height - 1: # Prevent writing outside the window
                break # Stop if we run out of lines in the status window

    status_win.refresh()

# ---
## Main Curses Application
def curses_main(stdscr, args, freq_map):
    curses.curs_set(0) # Hide the cursor

    # --- Initialize Colors ---
    if not curses.has_colors():
        stdscr.addstr(0, 0, "Your terminal does not support colors. Exiting.")
        stdscr.refresh()
        time.sleep(3)
        return

    curses.start_color() # Must be called to use colors

    # Define color pairs: (pair_id, foreground_color, background_color)
    curses.init_pair(COLOR_RED_ON_BLACK, curses.COLOR_RED, curses.COLOR_BLACK)
    curses.init_pair(COLOR_GREEN_ON_BLACK, curses.COLOR_GREEN, curses.COLOR_BLACK)
    curses.init_pair(COLOR_WHITE_ON_BLACK, curses.COLOR_WHITE, curses.COLOR_BLACK) # General text
    curses.init_pair(COLOR_CYAN_ON_BLACK, curses.COLOR_CYAN, curses.COLOR_BLACK) # For box titles
    curses.init_pair(COLOR_YELLOW_ON_BLACK, curses.COLOR_YELLOW, curses.COLOR_BLACK) # For status panel
    stdscr.bkgd(curses.color_pair(COLOR_WHITE_ON_BLACK))
    # --- End Initialize Colors ---

    port_paths = find_serial_ports(VENDOR_ID, PRODUCT_ID)

    if not port_paths:
        stdscr.addstr(0, 0, f"No serial ports found with Vendor ID {VENDOR_ID} and Product ID {PRODUCT_ID}")
        stdscr.refresh()
        time.sleep(3)
        return

    stdscr.addstr(0, 0, f"Used ports: {', '.join(port_paths)}")
    stdscr.refresh()

    height, width = stdscr.getmaxyx()

    # Calculate main content window height (leaving space for status panel)
    status_panel_height = max(3, len(port_paths) + 2) # At least 3 lines, or enough for ports + border
    main_content_height = height - 1 - status_panel_height # Total height - stdscr initial message - status panel height

    if main_content_height < 3: # Ensure main content area is still usable
        stdscr.addstr(1, 0, "Terminal height too small for both log and status panels.")
        stdscr.refresh()
        time.sleep(3)
        return

    col_width = width // len(port_paths)

    windows = []
    serial_ports = []

    # Create content windows for each serial port
    for idx, path in enumerate(port_paths):
        try:
            ser = serial.Serial(path, BAUD_RATE, timeout=1)
            serial_ports.append(ser)

            # Windows for port logs
            win_y = 1 # Start drawing windows from row 1 (below stdscr message)

            if col_width < 3:
                raise ValueError("Terminal width too small for multiple port displays.")

            win = curses.newwin(main_content_height, col_width, win_y, idx * col_width)
            win.box() # Draw the border first

            # Overwrite a section of the top border with the port name.
            port_name_display = f" {path} "
            start_x_for_title = max(1, (col_width - len(port_name_display)) // 2)
            win.addstr(0, start_x_for_title, port_name_display, curses.A_BOLD | curses.color_pair(COLOR_CYAN_ON_BLACK))

            win.scrollok(True) # Enable scrolling for the window
            win.bkgd(curses.color_pair(COLOR_WHITE_ON_BLACK)) # Set background color for the window
            win.refresh()
            windows.append(win)

            threading.Thread(target=read_from_port, args=(ser, win), daemon=True).start()


        except serial.SerialException as e:
            stdscr.addstr(idx + 1, 0, f"Could not open {path}: {e}", curses.color_pair(COLOR_RED_ON_BLACK))
            stdscr.refresh()
        except ValueError as e:
            stdscr.addstr(idx + 1, 0, str(e), curses.color_pair(COLOR_RED_ON_BLACK))
            stdscr.refresh()

    # Create the status panel window at the bottom
    status_win = curses.newwin(status_panel_height, width, height - status_panel_height, 0)
    status_win.box()
    status_win.bkgd(curses.color_pair(COLOR_YELLOW_ON_BLACK)) # Background for status panel

    # Initial draw of status panel title
    status_win.addstr(0, (width - len(" Message Counts ")) // 2, " Message Counts ", curses.A_BOLD | curses.color_pair(COLOR_YELLOW_ON_BLACK))
    status_win.refresh()


    for idx, port in enumerate(serial_ports):
        #threading.Thread(target=initialize_port, args=(port, idx), daemon=True).start()
        initialize_port(port, idx, args, freq_map)

    try:
        while True:
            # Periodically update the status panel
            update_status_panel(stdscr, status_win, port_paths)
            time.sleep(0.5) # Refresh status panel every 0.5 seconds
    except KeyboardInterrupt:
        pass
    finally:
        for port in serial_ports:
            if port.is_open:
                port.close()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Set transmission parameters")
    parser.add_argument(
        "--freq", nargs='*', default=[],
        help="Set per-node frequency as index-value pairs, e.g., --freq 1 868100000 2 867500000, if not provided 868000000 will be assumed"
    )
    parser.add_argument("--interval", type=int, default=30000, help="Set +INTERVAL value (ms)")
    parser.add_argument("--mindelta", type=int, default=0, help="Set +MINDELTA value (ms)")
    parser.add_argument("--maxdelta", type=int, default=30000, help="Set +MAXDELTA value (ms)")
    args = parser.parse_args()

    freq_map = {}
    freq_args = args.freq
    if len(freq_args) % 2 != 0:
        raise ValueError("Frequency arguments must be in index-value pairs (e.g. --freq 1 868100000)")

    for i in range(0, len(freq_args), 2):
        idx = int(freq_args[i])
        freq = freq_args[i + 1]
        freq_map[idx] = freq

    # Pass args to curses_main via lambda
    curses.wrapper(lambda stdscr: curses_main(stdscr, args, freq_map))
    #curses.wrapper(curses_main)