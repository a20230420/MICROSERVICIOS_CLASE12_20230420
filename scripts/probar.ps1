. "$PSScriptRoot\comun.ps1"
$script:checks = 0
function Check([string]$Url, [string]$Expected) {
    $result = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
    if ($result.StatusCode -ne 200 -or $result.Content.Trim() -ne $Expected) {
        throw "Respuesta inesperada en $Url : $($result.Content)"
    }
    $script:checks++
    Write-Host "OK $Url"
}
foreach ($service in $Services) {
    $health = Invoke-RestMethod "http://localhost:$($service.Port)/actuator/health" -TimeoutSec 10
    if ($health.status -ne 'UP') { throw "No esta listo $($service.Name)" }
    $script:checks++
}
foreach ($name in @('hello-service','addition-service','calculator-service')) {
    $registration = Invoke-RestMethod "http://localhost:8761/eureka/apps/$name" -Headers @{ Accept = 'application/json' } -TimeoutSec 10
    if (@($registration.application.instance | Where-Object status -eq 'UP').Count -lt 1) {
        throw "No esta registrado $name"
    }
    $script:checks++
}
Check 'http://localhost:8001/greeting' "Hello from 'hello-service'!"
Check 'http://localhost:8010/addition/1/5' '(1 + 5) = 6'
Check 'http://localhost:8020/addcalculator/1/5' 'Calculator : (1 + 5) = 6'
Check 'http://localhost:8020/addcalculator/-8/3' 'Calculator : (-8 + 3) = -5'
Check 'http://localhost:8010/addition/2147483647/1' '(2147483647 + 1) = 2147483648'
$code = 0
try {
    $response = Invoke-WebRequest 'http://localhost:8020/addcalculator/texto/3' -UseBasicParsing -TimeoutSec 10
    $code = [int]$response.StatusCode
} catch {
    if ($_.Exception.Response) { $code = [int]$_.Exception.Response.StatusCode }
    else { throw }
}
if ($code -ne 400) { throw "Se esperaba HTTP 400 para texto en lugar de numero; recibido $code" }
$script:checks++
Write-Host "$script:checks comprobaciones HTTP correctas."
