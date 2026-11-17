$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$RuntimeDir = Join-Path $ProjectRoot '.run'
$StateFile = Join-Path $RuntimeDir 'procesos.json'
$Services = @(
    @{ Name = 'eureka-server'; Port = 8761 },
    @{ Name = 'hello-service'; Port = 8001 },
    @{ Name = 'addition-service'; Port = 8010 },
    @{ Name = 'calculator-service'; Port = 8020 }
)

function Get-JarPath([string]$Name) {
    Join-Path $ProjectRoot "$Name\target\$Name-1.0.0.jar"
}

function Get-OwnedProcess($Entry) {
    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $($Entry.ProcessId)" -ErrorAction SilentlyContinue
    $jar = Get-JarPath $Entry.Name
    if ($process -and $process.Name -eq 'java.exe' -and
        $process.CommandLine -and $process.CommandLine.Contains($jar)) {
        return $process
    }
    return $null
}

function Wait-Http([string]$Url, [int]$Seconds = 120) {
    $deadline = (Get-Date).AddSeconds($Seconds)
    do {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 3
            if ($response.StatusCode -eq 200) { return }
        } catch { }
        Start-Sleep -Seconds 2
    } while ((Get-Date) -lt $deadline)
    throw "No respondio $Url. Revisa los archivos de .run."
}
