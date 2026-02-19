# Drinky Database Setup Script for Windows PowerShell
# Requires PostgreSQL to be installed and psql in PATH

param(
    [string]$PostgresUser = "postgres",
    [string]$DrinkyPassword = "drinky"
)

Write-Host "Setting up Drinky database..." -ForegroundColor Green

# Create database
Write-Host "Creating database drinky_db..."
psql -U $PostgresUser -c "CREATE DATABASE drinky_db WITH ENCODING = 'UTF8';"

# Create user
Write-Host "Creating user drinky..."
psql -U $PostgresUser -c "CREATE USER drinky WITH PASSWORD '$DrinkyPassword';"

# Grant privileges
Write-Host "Granting privileges..."
psql -U $PostgresUser -c "GRANT ALL PRIVILEGES ON DATABASE drinky_db TO drinky;"
psql -U $PostgresUser -d drinky_db -c "GRANT ALL ON SCHEMA public TO drinky;"
psql -U $PostgresUser -d drinky_db -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO drinky;"
psql -U $PostgresUser -d drinky_db -c "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO drinky;"

# Enable UUID extension
Write-Host "Enabling UUID extension..."
psql -U $PostgresUser -d drinky_db -c "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";"

Write-Host "Database setup complete!" -ForegroundColor Green
Write-Host "Connection URL: jdbc:postgresql://localhost:5432/drinky_db" -ForegroundColor Yellow
Write-Host "Username: drinky" -ForegroundColor Yellow
Write-Host "Password: $DrinkyPassword" -ForegroundColor Yellow
