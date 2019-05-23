#!/bin/bash
SERVICE="java"
if pgrep -x "$SERVICE" >/dev/null
then
    echo "bot is online"
else
    cd /var/www && mvn spring-boot:run &> "/var/www/logs/$(date +"%m_%d_%Y").log"
fi