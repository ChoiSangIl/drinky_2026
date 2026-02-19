#!/bin/bash
# Drinky Server Setup Script
# Run as root on the target Linux server
# Usage: sudo ./server-setup.sh

set -e

echo "=== Drinky Server Setup ==="

# 1. Create system user
echo "Creating drinky user..."
if ! id "drinky" &>/dev/null; then
    useradd -r -m -d /opt/drinky -s /bin/bash drinky
    echo "User 'drinky' created"
else
    echo "User 'drinky' already exists"
fi

# 2. Create directory structure
echo "Creating directory structure..."
mkdir -p /opt/drinky/{app,logs,backups}
chown -R drinky:drinky /opt/drinky

# 3. Create environment file
echo "Creating environment file template..."
cat > /opt/drinky/.env.prod << 'EOF'
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/drinky_db
DATABASE_USERNAME=drinky
DATABASE_PASSWORD=CHANGE_ME

# OAuth - Google
GOOGLE_CLIENT_ID=CHANGE_ME
GOOGLE_CLIENT_SECRET=CHANGE_ME

# OAuth - Kakao
KAKAO_CLIENT_ID=CHANGE_ME
KAKAO_CLIENT_SECRET=CHANGE_ME

# Firebase (optional)
FIREBASE_CREDENTIALS_PATH=/opt/drinky/firebase-service-account.json

# App
APP_BASE_URL=https://your-domain.iptime.org
SPRING_PROFILES_ACTIVE=prod
EOF
chown drinky:drinky /opt/drinky/.env.prod
chmod 600 /opt/drinky/.env.prod

# 4. Create systemd service
echo "Creating systemd service..."
cat > /etc/systemd/system/drinky.service << 'EOF'
[Unit]
Description=Drinky Application
After=syslog.target network.target postgresql.service
Requires=postgresql.service

[Service]
Type=simple
User=drinky
Group=drinky
WorkingDirectory=/opt/drinky/app
EnvironmentFile=/opt/drinky/.env.prod
ExecStart=/usr/bin/java -Xms256m -Xmx512m -Dspring.profiles.active=prod -jar drinky.jar
Restart=always
RestartSec=10
SuccessExitStatus=143
StandardOutput=append:/opt/drinky/logs/app.log
StandardError=append:/opt/drinky/logs/error.log

[Install]
WantedBy=multi-user.target
EOF

# 5. Reload systemd
echo "Reloading systemd..."
systemctl daemon-reload

# 6. Enable service (but don't start - no JAR yet)
echo "Enabling drinky service..."
systemctl enable drinky

echo ""
echo "=== Server Setup Complete ==="
echo ""
echo "Next steps:"
echo "1. Edit /opt/drinky/.env.prod with your credentials"
echo "2. Configure Nginx (see scripts/nginx-setup.sh)"
echo "3. Deploy the JAR file to /opt/drinky/app/"
echo "4. Start service: sudo systemctl start drinky"
echo ""
