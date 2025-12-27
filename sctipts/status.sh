#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🐇 BunnyNotifier Infrastructure Status${NC}"
echo -e "${BLUE}====================================${NC}\n"

# Check Docker Compose status
echo -e "${YELLOW}📋 Container Status:${NC}"
docker-compose ps

echo -e "\n${YELLOW}📊 Resource Usage:${NC}"
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}" \
  $(docker-compose ps -q) 2>/dev/null || echo "No containers running"

echo -e "\n${YELLOW}🌐 Network Information:${NC}"
docker network inspect bunny-network --format '{{range .Containers}}{{.Name}} ({{.IPv4Address}}){{"\n"}}{{end}}' 2>/dev/null || echo "Network not found"

echo -e "\n${YELLOW}🔗 Quick Access Links:${NC}"
echo -e "RabbitMQ Management: ${BLUE}http://localhost:15672${NC}"
echo -e "RabbitMQ Metrics:    ${BLUE}http://localhost:9090/metrics${NC}"
echo -e "AMQP Endpoint:       ${BLUE}amqp://bunny:carrot123@localhost:5672${NC}"

echo -e "\n${YELLOW}📝 Logs (last 5 lines):${NC}"
docker-compose logs --tail=5