param([switch]$SinCompilar)
. "$PSScriptRoot\comun.ps1"

if (Test-Path -LiteralPath $StateFile) {
    $entries = Get-Content -LiteralPath $StateFile -Raw | ConvertFrom-Json
    foreach ($entry in $entries) {
        if (Get-OwnedProcess $entry) {
            throw 'El ejemplo ya esta iniciado. Usa detener.cmd antes de iniciarlo otra vez.'
        }
    }
}
foreach ($service in $Services) {
    if (Get-NetTCPConnection -LocalPort $service.Port -State Listen -ErrorAction SilentlyContinue) {
        throw "El puerto $($service.Port) esta ocupado. Cierra esa aplicacion o cambia su puerto."
    }
}
if ($env:JAVA_HOME) {
    $java = Join-Path $env:JAVA_HOME 'bin\java.exe'
} else {
    $java = (Get-Command java.exe -ErrorAction Stop).Source
}
if (!(Test-Path -LiteralPath $java)) { throw 'Revisa JAVA_HOME: no se encontro java.exe.' }
Write-Host 'Java seleccionado:' $java
& $java -version
if (!$SinCompilar) {
    Push-Location $ProjectRoot
    try {
        & '.\mvnw.cmd' -B -ntp verify
        if ($LASTEXITCODE -ne 0) { throw 'Fallo Maven. Lee el primer ERROR de la compilacion.' }
    } finally { Pop-Location }
}
foreach ($service in $Services) {
    if (!(Test-Path -LiteralPath (Get-JarPath $service.Name))) {
        throw 'Faltan los JAR. Ejecuta iniciar.cmd para compilar primero.'
    }
}
New-Item -ItemType Directory -Force -Path $RuntimeDir | Out-Null
$started = @()
try {
    foreach ($service in $Services) {
        $jar = Get-JarPath $service.Name
        $process = Start-Process -FilePath $java -ArgumentList @('-Xms64m', '-Xmx256m', '-jar', "`"$jar`"") `
            -WorkingDirectory $ProjectRoot -WindowStyle Hidden -PassThru `
            -RedirectStandardOutput (Join-Path $RuntimeDir "$($service.Name).log") `
            -RedirectStandardError (Join-Path $RuntimeDir "$($service.Name).error.log")
        $started += [pscustomobject]@{ Name = $service.Name; ProcessId = $process.Id; Port = $service.Port }
        ConvertTo-Json -InputObject @($started) | Set-Content -LiteralPath $StateFile -Encoding UTF8
        Write-Host "Iniciando $($service.Name) en $($service.Port)..."
        Wait-Http "http://localhost:$($service.Port)/actuator/health"
    }
    Write-Host 'Esperando que Calculator descubra Addition mediante Eureka...'
    Wait-Http 'http://localhost:8020/addcalculator/1/5'
    Write-Host 'LISTO. Eureka: http://localhost:8761'
    Write-Host 'Prueba: http://localhost:8020/addcalculator/1/5'
    Write-Host 'Para detener todo: .\detener.cmd'
} catch {
    foreach ($entry in $started) {
        if (Get-OwnedProcess $entry) { Stop-Process -Id $entry.ProcessId -ErrorAction SilentlyContinue }
    }
    throw
}
