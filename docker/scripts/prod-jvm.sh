#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting Production Environment (JVM)${NC}"
echo -e "${GREEN}App: http://localhost:8080${NC}"
echo ""

cd "$(dirname "$0")/../prod/jvm"
docker compose up -d --build

