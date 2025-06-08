param (
    [string[]]$ports
)

$PioMessageCoreLogic = {
    param (
        [Parameter(Mandatory=$true)]
        [string]$Message,

        [Parameter(Mandatory=$true)]
        [System.ConsoleColor]$ContentColor,

        [string]$Port = $null
    )

    $PrefixColor = "Gray"
    $timestamp = (Get-Date -Format "yyyy-MM-dd HH:mm:ss.fff")

    $Prefix = if ($Port) { "$timestamp [PIO|$Port]" } else { "$timestamp [PIO]" }

    Write-Host $Prefix -ForegroundColor $PrefixColor -NoNewline
    Write-Host " $Message" -ForegroundColor $ContentColor
}

function Write-PioMessage {
    & $PioMessageCoreLogic @Args
}

if (-not $ports -or $ports.Count -eq 0) {
    Write-PioMessage -Message "No ports specified. Discovering devices..." -ContentColor Yellow
    $jsonOutput = pio device list --json-output

    if ($jsonOutput) {
        try {
            $devices = ConvertFrom-Json $jsonOutput
            $filteredPorts = $devices | Where-Object { $_.hwid -like "*VID:PID=10C4:EA60*" } | Select-Object -ExpandProperty port

            if ($filteredPorts) {
                $ports = $filteredPorts
                Write-PioMessage -Message "Discovered ports: $($ports -join ', ')" -ContentColor Cyan
            } else {
                Write-PioMessage -Message "No compatible devices found with VID:PID=10C4:EA60." -ContentColor Red
                exit 1
            }
        } catch {
            Write-PioMessage -Message "Failed to parse pio device list output: $($_.Exception.Message)" -ContentColor Red
            exit 1
        }
    } else {
        Write-PioMessage -Message "Failed to get device list from 'pio device list --json-output'." -ContentColor Red
        exit 1
    }
}

Write-PioMessage -Message "Used ports: $($ports -join ', ')" -ContentColor Cyan

Write-PioMessage -Message "Starting build..." -ContentColor Blue
pio run -e main *>$null
$buildExitCode = $LASTEXITCODE

if ($buildExitCode -ne 0) {
    Write-PioMessage -Message "Build failed! (Exit Code: $($buildExitCode)). Aborting upload." -ContentColor Red
    exit 1
}

Write-PioMessage -Message "Build successful!" -ContentColor Green

$jobs = @()

foreach ($p in $ports) {
    Write-PioMessage -Message "Starting upload job for port..." -ContentColor Blue -Port $p
    $job = Start-Job -ScriptBlock {
        param(
            $port_name,
            $PioMessageCoreLogicFromParent
        )

        function Write-PioMessage {
            & $PioMessageCoreLogicFromParent @Args
        }

        pio run -t nobuild -t upload -e main --upload-port $port_name *>$null
        $exitCode = $LASTEXITCODE

        [PSCustomObject]@{
            Port = $port_name
            ExitCode = $exitCode
        }
    } -Name $p -ArgumentList $p, $PioMessageCoreLogic
    $jobs += $job
}

Write-PioMessage -Message "Waiting for all upload jobs to complete and displaying results as they finish..." -ContentColor Blue

while ($jobs.Count -gt 0) {
    $completedJob = Wait-Job -Job $jobs -Any -Timeout 1

    if ($completedJob) {
        $result = Receive-Job -Job $completedJob

        if ($result) {
            if ($result.ExitCode -eq 0) {
                Write-PioMessage -Message "Upload to port successful!" -ContentColor Green -Port $result.Port
            } else {
                Write-PioMessage -Message "Upload to port failed! (Exit Code: $($result.ExitCode))" -ContentColor Red -Port $result.Port
            }
        } else {
            Write-PioMessage -Message "Job completed, but no result was returned." -ContentColor Yellow -Port $completedJob.Name
        }

        $jobs = $jobs | Where-Object { $_.Id -ne $completedJob.Id }
        Remove-Job -Job $completedJob | Out-Null
    }
}

Write-PioMessage -Message "All upload processes completed." -ContentColor Blue
