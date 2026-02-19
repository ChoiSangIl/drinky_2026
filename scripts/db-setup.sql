-- Drinky Database Setup Script
-- Run as PostgreSQL superuser (e.g., postgres)

-- 1. Create database
CREATE DATABASE drinky_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- 2. Create user
CREATE USER drinky WITH PASSWORD 'your_secure_password_here';

-- 3. Grant privileges
GRANT ALL PRIVILEGES ON DATABASE drinky_db TO drinky;

-- 4. Connect to drinky_db and grant schema privileges
\c drinky_db
GRANT ALL ON SCHEMA public TO drinky;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO drinky;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO drinky;

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
