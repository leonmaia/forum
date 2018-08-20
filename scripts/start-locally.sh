#!/usr/bin/env bash

./scripts/start-forumdb.sh

port=`docker inspect -f '{{ (index (index .NetworkSettings.Ports "5432/tcp") 0).HostPort }}' forumdb`

./sbt -Dorgsdb.port=${port} start
