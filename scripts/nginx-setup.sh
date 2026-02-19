#!/bin/bash
# Drinky Nginx Setup Script
# Run as root on the target Linux server
# Usage: sudo ./nginx-setup.sh [domain]

set -e

DOMAIN="${1:-your-domain.iptime.org}"

echo "=== Drinky Nginx Setup ==="
echo "Domain: $DOMAIN"

# Check if nginx is installed
if ! command -v nginx &> /dev/null; then
    echo "Nginx not found. Installing..."
    apt update && apt install -y nginx
fi

# Create nginx configuration
echo "Creating Nginx configuration..."
cat > /etc/nginx/sites-available/drinky << EOF
server {
    listen 80;
    server_name $DOMAIN;

    # Redirect HTTP to HTTPS
    return 301 https://\$server_name\$request_uri;
}

server {
    listen 443 ssl http2;
    server_name $DOMAIN;

    # SSL certificates (Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/$DOMAIN/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/$DOMAIN/privkey.pem;

    # SSL configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    # Proxy to Spring Boot
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;

        # WebSocket support (if needed)
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Static files caching
    location /static/ {
        proxy_pass http://127.0.0.1:8080/static/;
        expires 7d;
        add_header Cache-Control "public, immutable";
    }

    # Service Worker - no cache
    location /sw.js {
        proxy_pass http://127.0.0.1:8080/sw.js;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }
}
EOF

# Enable site
echo "Enabling site..."
ln -sf /etc/nginx/sites-available/drinky /etc/nginx/sites-enabled/

# Test nginx configuration
echo "Testing Nginx configuration..."
nginx -t

echo ""
echo "=== Nginx Setup Complete ==="
echo ""
echo "Next steps:"
echo "1. Obtain SSL certificate: sudo certbot --nginx -d $DOMAIN"
echo "2. Reload nginx: sudo systemctl reload nginx"
echo "3. Open firewall port 443: sudo ufw allow 443/tcp"
echo ""
