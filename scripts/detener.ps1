. "$PSScriptRoot\comun.ps1"
if (!(Test-Path -LiteralPath $StateFile)) {
    Write-Host 'No hay procesos registrados por este proyecto.'
    return
}
$entries = Get-Content -LiteralPath $StateFile -Raw | ConvertFrom-Json
foreach ($entry in $entries) {
    if (Get-OwnedProcess $entry) {
        Stop-Process -Id $entry.ProcessId -ErrorAction Stop
        Write-Host "Detenido: $($entry.Name)"
    } else {
        Write-Host "Ya no esta activo: $($entry.Name)"
    }
}
Remove-Item -LiteralPath $StateFile
