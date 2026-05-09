#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting Development Environment${NC}"
echo -e "${GREEN}App: http://localhost:8080${NC}"
echo -e "${GREEN}Debug: localhost:5005${NC}"
echo -e "${GREEN}RabbitMQ Management: http://localhost:15672 (guest/guest)${NC}"
echo ""

cd "$(dirname "$0")/../dev"
docker-compose up -d --build