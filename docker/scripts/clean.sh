#!/bin/bash

RED='\033[0;31m'
NC='\033[0m'

echo -e "${RED}🧹 Cleaning all volumes and cache${NC}"

cd "$(dirname "$0")/../dev"
docker-compose down -v

cd "$(dirname "$0")/../prod/native"
docker-compose down -v

cd "$(dirname "$0")/../prod/jvm"
docker-compose down -v

echo -e "${RED}✅ Cleanup completed${NC}"