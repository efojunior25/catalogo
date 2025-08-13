Write-Host "Starting development environment..." -ForegroundColor Green

if (!(Test-Path "backend") -or !(Test-Path "frontend")) {
    Write-Host "Run this scrip in the project root!" -ForegroundColor Red
    exit 1
}

function Start-Backend {
    Write-Host "Starting Backend..." -ForegroundColor Blue
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; ./mvnw.cmd spring-boot:run"
}

function Start-Frontend {
    if (Test-Path "frontend/package.json") {
        Write-Host "Starting Frontend..." -ForegroundColor Blue
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm start"
    } else {
        Write-Host "Frontend not yet configured" -ForegroundColor Yellow
    }
}

Start-Backend
Start-Sleep -Seconds 3
Start-Frontend

Write-Host "Services Started" -ForegroundColor Green
Write-Host "Backend: http://localhost:8080/api/v1" -ForegroundColor Cyan
Write-Host "Frontend: http://localhost:3000" -ForegroundColor Cyan
Write-Host "H2 Console: http://localhost:8080/api/v1/h2-console" -ForegroundColor Cyan