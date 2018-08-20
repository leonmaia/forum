#!/bin/bash
BASE_IMAGE_NAME=scale/forum-ci
BUILD_IMAGE_NAME=scale/forum-build

# build base image
docker build -f docker-ci/Dockerfile -t ${BASE_IMAGE_NAME} .

# build test runner image
docker build -f docker-ci/Dockerfile.build -t ${BUILD_IMAGE_NAME} .

# run tests
docker run -v /var/run/docker.sock:/var/run/docker.sock -v `pwd`:/opt/build/ ${BUILD_IMAGE_NAME}

docker rmi -f ${BUILD_IMAGE_NAME}