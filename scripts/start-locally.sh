#!/usr/bin/env bash

./scripts/start-forumdb.sh

port=`docker inspect -f '{{ (index (index .NetworkSettings.Ports "5432/tcp") 0).HostPort }}' forumdb`

./scripts/start-redisdb.sh

port2=`docker inspect -f '{{ (index (index .NetworkSettings.Ports "6379/tcp") 0).HostPort }}' redisdb`

./sbt -Dforumdb.port=${port} -Dredisdb.port=${port2} start
