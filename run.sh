#!/bin/bash
set -e

echo "=========================================================="
echo " Starting E-Tube Streaming Platform Backend stack "
echo "=========================================================="

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "Error: Docker daemon is not running. Please start Docker and try again."
    exit 1
fi

echo "Spinning up PostgreSQL, MongoDB, Redis, RabbitMQ and Spring Boot..."
docker compose up --build -d

echo "----------------------------------------------------------"
echo "Services are launching! It may take a minute for the Spring"
echo "Boot application to fully start compile-and-run inside Docker."
echo "----------------------------------------------------------"
echo "Useful URLs:"
echo " - Spring Boot API host : http://localhost:8080"
echo " - RabbitMQ Dashboard   : http://localhost:15672 (user: guest / pass: guest)"
echo "----------------------------------------------------------"
echo "To view application logs, run:"
echo "  docker compose logs -f app"
echo "=========================================================="
