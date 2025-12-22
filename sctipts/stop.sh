#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🛑 Stopping BunnyNotifier infrastructure...${NC}"

# Stop and remove containers
docker-compose down

# Check if we should remove volumes too
if [[ "$1" == "--clean" ]] || [[ "$1" == "-c" ]]; then
    echo -e "${YELLOW}🧹 Removing volumes...${NC}"
    docker volume rm bunny-rabbitmq-data 2>/dev/null || true
    echo -e "${GREEN}✅ Volumes removed${NC}"
fi

# Check if we should remove network too
if [[ "$1" == "--full" ]] || [[ "$1" == "-f" ]]; then
    echo -e "${YELLOW}🌐 Removing network...${NC}"
    docker network rm bunny-network 2>/dev/null || true
    echo -e "${GREEN}✅ Network removed${NC}"
fi

echo -e "${GREEN}✅ Infrastructure stopped successfully!${NC}"