#!/bin/bash

sbtver=1.2.1
sbtjar=sbt-launch.jar
sbtsha128=9e997833c7980b71feaae552bf454f292268c4ee

sbtrepo=http://central.maven.org/maven2/org/scala-sbt/sbt-launch

if [ ! -f $sbtjar ]; then
  echo "downloading $PWD/$sbtjar" 1>&2
  if ! curl --location --silent --fail --remote-name $sbtrepo/$sbtver/$sbtjar; then
    exit 1
  fi
fi

checksum=`openssl dgst -sha1 $sbtjar | awk '{ print $2 }'`
if [ "$checksum" != $sbtsha128 ]; then
  echo "bad $PWD/$sbtjar.  delete $PWD/$sbtjar and run $0 again."
  exit 1
fi

[ -f ~/.sbtconfig ] && . ~/.sbtconfig

# the -DSKIP_SBT flag is set to skip tests that shouldn't be run with sbt.
java -ea                          \
  $SBT_OPTS                       \
  $JAVA_OPTS                      \
  -Djava.net.preferIPv4Stack=true \
  -XX:+AggressiveOpts             \
  -XX:+UseParNewGC                \
  -XX:+UseConcMarkSweepGC         \
  -XX:+CMSParallelRemarkEnabled   \
  -XX:+CMSClassUnloadingEnabled   \
  -XX:ReservedCodeCacheSize=128m  \
  -XX:SurvivorRatio=128           \
  -XX:MaxTenuringThreshold=0      \
  -Xss8M                          \
  -Xms512M                        \
  -Xmx2G                          \
  -DSKIP_SBT=1                    \
  -server                         \
  -jar $sbtjar "$@"