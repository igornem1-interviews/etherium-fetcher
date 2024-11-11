#!/bin/bash
docker network create limeapi-network
sudo docker-compose -f docker-compose-limeapi-db.yaml up --build -d 
./docker-status.sh

