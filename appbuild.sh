#!/bin/bash
sudo docker build -f Dockerfile.limeapi -t limeapi .
./docker-status.sh