#!/usr/bin/env bash

current_id=`docker inspect --format="{{.Id}}" redisdb`

if [ $? -eq 0 ]; then
    printf "Removing old instance with id ${current_id}\n"
    docker rm -f ${current_id}
fi

docker run --name redisdb -p 6379:6379 -d redis:3.2.12-alpine

port=`docker inspect -f '{{ (index (index .NetworkSettings.Ports "6379/tcp") 0).HostPort }}' redisdb`

printf "\nRedis db running on port ${port}\n"
