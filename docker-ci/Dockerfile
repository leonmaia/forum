FROM openjdk:8u131-jre-alpine
LABEL maintainer="Leon Maia leontmaia@gmail.com"

RUN apk add --no-cache curl bash openssl bc

ENV JAVA_HOME "/opt/jdk"
ENV SBT_VERSION 1.2.1
ENV SCALA_VERSION 2.12.6

ENV DEPS_HOME /opt/deps
ENV SBT_HOME ${DEPS_HOME}/sbt-${SBT_VERSION}
ENV SCALA_HOME ${DEPS_HOME}/scala-${SCALA_VERSION}

# Install Sbt & Scala
RUN mkdir -p ${SBT_HOME} && \
    mkdir -p ${SCALA_HOME} && \
    cd /root && \
    curl -o scala-$SCALA_VERSION.tgz http://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz && \
    tar -xf scala-$SCALA_VERSION.tgz -C $DEPS_HOME && \
    rm scala-$SCALA_VERSION.tgz && \
    echo -ne "- with scala $SCALA_VERSION\n" >> /root/.built && \
    curl -sL sbt-$SBT_VERSION.tgz https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz | gunzip | tar -x -C $SBT_HOME && \
    cp -rf $SBT_HOME/sbt/bin/ $SBT_HOME/bin/ && \
    cp -rf $SBT_HOME/sbt/conf/ $SBT_HOME/conf/ && \
    rm -rf sbt-$SBT_VERSION.tgz && \
    rm -rf $SBT_HOME/sbt && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built && \
    mkdir -p ~/.sbt/1.0/plugins

WORKDIR /tmp

ENV SBT_OPTS "-Dsbt.ivy.home=/tmp/.ivy2 -Dsbt.global.base=/tmp/.sbt"

ENV PATH=${PATH}:${JAVA_HOME}/bin:${SBT_HOME}/bin:${SCALA_HOME}/bin