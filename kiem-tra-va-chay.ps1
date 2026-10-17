$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host "`n=== SMART CRM L4: KIEM TRA VA CHAY ===" -ForegroundColor Cyan
Write-Host "Buoc 1/2: Clean, test va dong goi..."
& .\mvnw.cmd clean test package
if ($LASTEXITCODE -ne 0) {
    Write-Host "`nBUILD/TEST THAT BAI. Hay xem loi phia tren." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "`nBuoc 2/2: Kiem tra cong 8081..."
$listener = Get-NetTCPConnection -LocalPort 8081 -State Listen -ErrorAction SilentlyContinue
if ($listener) {
    Write-Host "Cong 8081 dang co ung dung lang nghe. Khong khoi dong ban thu hai." -ForegroundColor Yellow
    Write-Host "Neu trang web dang mo, truy cap: http://localhost:8081/"
    Write-Host "Neu muon chay lai tu script, dong tien trinh dang dung cong 8081 roi chay lai."
    exit 0
}

Write-Host "Kiem thu thanh cong. Dang khoi dong Spring Boot..." -ForegroundColor Green
& .\mvnw.cmd spring-boot:run
exit $LASTEXITCODE
