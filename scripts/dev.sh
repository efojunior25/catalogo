#!/bin/bash

echo -e "\033[32mStarting development environment...\033[0m"

if [[ ! -d "backend" || ! -d "frontend" ]]; then
    echo -e "\033[31mRun this script in the project root!\033[0m"
    exit 1
fi

start_backend() {
    echo -e "\033[34mStarting Backend...\033[0m"
    (cd backend && ./mvnw spring-boot:run)
}

start_frontend() {
    if [[ -f "frontend/package.json" ]]; then
        echo -e "\033[34mStarting Frontend...\033[0m"
        (cd frontend && npm start)
    else
        echo -e "\033[33mFrontend not yet configured\033[0m"
    fi
}

gnome-terminal -- bash -c "cd backend && ./mvnw spring-boot:run; exec bash" &

sleep 3

gnome-terminal -- bash -c "cd frontend && npm start; exec bash" &

echo -e "\033[32mServices Started\033[0m"
echo -e "\033[36mBackend: http://localhost:8080/api/v1\033[0m"
echo -e "\033[36mFrontend: http://localhost:3000\033[0m"
echo -e "\033[36mH2 Console: http://localhost:8080/h2-console\033[0m"