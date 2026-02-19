#!/bin/bash
# Manual deployment script for Drinky
# Usage: ./scripts/deploy.sh [server_host] [server_user]

set -e

SERVER_HOST="${1:-your-server.iptime.org}"
SERVER_USER="${2:-drinky}"
SERVER_PORT="${3:-22}"
REMOTE_PATH="/opt/drinky/app"

echo "=== Drinky Deployment Script ==="
echo "Server: $SERVER_USER@$SERVER_HOST:$SERVER_PORT"
echo ""

# Build TailwindCSS
echo "Building TailwindCSS..."
cd frontend
npm ci
npm run build:css
cd ..

# Build application
echo "Building application..."
./gradlew clean build -x test

# Copy JAR to server
echo "Copying JAR to server..."
scp -P $SERVER_PORT build/libs/drinky.jar $SERVER_USER@$SERVER_HOST:$REMOTE_PATH/

# Restart service
echo "Restarting service..."
ssh -p $SERVER_PORT $SERVER_USER@$SERVER_HOST "sudo systemctl restart drinky && sleep 3 && sudo systemctl status drinky"

echo ""
echo "=== Deployment complete! ==="
echo "Check: https://$SERVER_HOST/health"
