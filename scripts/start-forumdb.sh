#!/usr/bin/env bash

current_id=`docker inspect --format="{{.Id}}" forumdb`

if [ $? -eq 0 ]; then
    printf "Removing old instance with id ${current_id}\n"
    docker rm -f ${current_id}
fi

docker run --name forumdb -p 5432:5432 -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=user -e POSTGRES_DB=forum -d postgres:10-alpine

port=`docker inspect -f '{{ (index (index .NetworkSettings.Ports "5432/tcp") 0).HostPort }}' forumdb`

printf "\nForum db running on port ${port}\n"
