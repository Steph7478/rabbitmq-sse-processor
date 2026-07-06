#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting Production Environment (Native Image)${NC}"
echo -e "${GREEN}App: http://localhost:8080${NC}"
echo -e "${BLUE}⚠️  First build may take several minutes${NC}"
echo ""

cd "$(dirname "$0")/../prod/native"
docker compose up -d --build

