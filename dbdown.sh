#!/bin/bash
sudo docker-compose -f docker-compose-limeapi-db.yaml down --volumes --rmi local
sudo docker network rm limeapi-network
./docker-status.sh

