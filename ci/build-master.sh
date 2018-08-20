#!/bin/bash

# Images
VERSION_HASH="$(git rev-parse HEAD)"
IMAGE_NAME=scale/forum-service
BASE_IMAGE_NAME=scale/forum-ci
BUILD_IMAGE_NAME=scale/forum-build

# build base image
docker build -f docker-ci/Dockerfile -t ${BASE_IMAGE_NAME} .

# build test runner image
docker build -f docker-ci/Dockerfile.build -t ${BUILD_IMAGE_NAME} .

# create jar
docker run -v `pwd`:/opt/build ${BUILD_IMAGE_NAME} sbt assembly

docker rmi -f ${BUILD_IMAGE_NAME}

# Build (tag: latest)
docker build --build-arg VERSION_HASH=${VERSION_HASH} -f docker/Dockerfile -t ${IMAGE_NAME} .

# Add version (tag: <version>)
docker tag ${IMAGE_NAME}:latest ${IMAGE_NAME}:${VERSION_HASH}
