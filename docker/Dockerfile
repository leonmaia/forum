FROM openjdk:8u131-jre-alpine
LABEL maintainer="Leon Maia leontmaia@gmail.com"

ARG VERSION_HASH=default
LABEL version=$VERSION_HASH

RUN apk add --no-cache curl bash

WORKDIR /opt/app/

ADD ./target/scala-2.12/forum.jar .

CMD java $JAVA_OPTIONS -jar forum.jar