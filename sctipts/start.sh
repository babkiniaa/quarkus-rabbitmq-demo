#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🐇 Starting BunnyNotifier infrastructure...${NC}"

# Check if docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed. Please install Docker first.${NC}"
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed. Please install Docker Compose first.${NC}"
    exit 1
fi

# Create network if it doesn't exist
if ! docker network inspect bunny-network &> /dev/null; then
    echo -e "${YELLOW}🌐 Creating bunny-network...${NC}"
    docker network create bunny-network
fi

# Start services
echo -e "${YELLOW}🚀 Starting RabbitMQ with Management UI...${NC}"
docker-compose up -d rabbitmq

# Wait for RabbitMQ to be healthy
echo -e "${YELLOW}⏳ Waiting for RabbitMQ to be ready...${NC}"
sleep 5

MAX_WAIT=60
ELAPSED=0
while [ $ELAPSED -lt $MAX_WAIT ]; do
    if docker-compose ps rabbitmq | grep -q "(healthy)"; then
        echo -e "${GREEN}✅ RabbitMQ is healthy and ready!${NC}"
        break
    fi

    echo -e "${YELLOW}⏳ Waiting... ($((ELAPSED + 5))s/${MAX_WAIT}s)${NC}"
    sleep 5
    ELAPSED=$((ELAPSED + 5))

    if [ $ELAPSED -ge $MAX_WAIT ]; then
        echo -e "${RED}❌ RabbitMQ failed to start within ${MAX_WAIT} seconds${NC}"
        docker-compose logs rabbitmq
        exit 1
    fi
done

# Start optional services
echo -e "${YELLOW}📊 Starting RabbitMQ Exporter for metrics...${NC}"
docker-compose up -d rabbitmq-exporter

# Display status
echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}🚀 Infrastructure started successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "${BLUE}📊 RabbitMQ Management UI:${NC} http://localhost:15672"
echo -e "${BLUE}👤 Username:${NC} bunny"
echo -e "${BLUE}🔑 Password:${NC} carrot123"
echo -e "${BLUE}📈 Metrics Exporter:${NC} http://localhost:9090/metrics"
echo -e "${BLUE}🐇 AMQP Port:${NC} localhost:5672"
echo -e "\n${YELLOW}To stop the infrastructure, run:${NC} ./scripts/stop.sh"
echo -e "${YELLOW}To view logs, run:${NC} ./scripts/status.sh"