#!/bin/bash

RED='\033[0;31m'
NC='\033[0m'

echo -e "${RED}🛑 Stopping all environments${NC}"

cd "$(dirname "$0")/../dev"
docker-compose down

cd "$(dirname "$0")/../prod/native"
docker-compose down

cd "$(dirname "$0")/../prod/jvm"
docker-compose down

echo -e "${RED}✅ All services stopped${NC}"