#!/bin/bash
sudo docker run -d --name limeapi-container -p 8001:8001 --network limeapi-network limeapi
./docker-status.sh
sudo docker logs -f limeapi-container
sudo docker logs 
