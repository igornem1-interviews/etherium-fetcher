#!/bin/bash
sudo docker-compose -f docker-compose.yaml up --build -d
./docker-status.sh

