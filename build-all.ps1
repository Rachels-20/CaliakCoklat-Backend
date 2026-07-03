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
    Write-Host "Building $service ..."
    Write-Host "========================================"

    Push-Location $service

    docker build -t $service .

    if ($LASTEXITCODE -ne 0) {
        Write-Host ""
        Write-Host "Build gagal pada $service"
        Pop-Location
        exit 1
    }

    Pop-Location
}

Write-Host ""
Write-Host "========================================"
Write-Host "SEMUA IMAGE BERHASIL DIBUILD"
Write-Host "========================================"