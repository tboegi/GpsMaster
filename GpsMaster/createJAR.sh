#!/bin/sh
rm -rf GpsMaster_jar
mkdir -p GpsMaster_jar &&
rsync -ar external/ GpsMaster_jar/ &&
rsync -ar src/* GpsMaster_jar/ --exclude=*.java &&
JVER=$(javac -version 2>&1 ) &&
# Check the java version
# Oracle Java 1.8 has been tested as well as Openjdk 11.0
case $JVER in
  javac*1[12].0.*)
    rsync -ar external-jdk11/ GpsMaster_jar/
    JDKVER=JDK11-12
  ;;
  javac*1.8.*)
    JDKVER=JDK7-8
  ;;
  *)
  echo >&2 "untested java version $JVER"
  echo >&2 "See createJAR.sh"
  exit 1
  ;;
esac
rsync -ar META-INF/ GpsMaster_jar/ &&
( cd GpsMaster_jar/ &&
    jar cmf MANIFEST.MF GpsMaster_XX.YY.$JDKVER.jar *
)
