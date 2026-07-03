$services = @(
    "api-gateway",
    "authentication-service",
    "dashboard-service",
    "device-service",
    "eureka",
    "kelembapan-tanah-service",
    "kelembapan-udara-service",
    "notification-service",
    "ph-tanah-service",
    "suhu-tanah-service",
    "suhu-udara-service",
    "weekly-report-service"
)

foreach ($service in $services) {

    Write-Host ""
    Write-Host "========================================"
    Write-Host "Packaging $service ..."
    Write-Host "========================================"

    Push-Location $service

    .\mvnw clean package -DskipTests

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "Package gagal pada $service"
        Pop-Location
        exit 1
    }

    Pop-Location
}

Write-Host ""
Write-Host "========================================"
Write-Host "SEMUA SERVICE BERHASIL DIPACKAGE"
Write-Host "========================================"