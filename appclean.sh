#!/bin/bash
sudo docker stop limeapi-container
sudo docker rm limeapi-container
sudo docker rmi limeapi
./docker-status.sh