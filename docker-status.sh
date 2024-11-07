#!/bin/bash
echo "* containers:"
sudo docker ps -a
echo ""
echo "* images:"
sudo docker images
echo ""
echo "* volumes:"
sudo docker volume ls
echo ""
echo "* networks:"
sudo docker network ls
echo ""

