#!/bin/sh
rm -rf GpsMaster_jar
mkdir -p GpsMaster_jar/org/gpsmaster/ &&
rsync  bin/org/gpsmaster/*.txt GpsMaster_jar/org/gpsmaster/ &&
rsync -ar external/ GpsMaster_jar/ &&
rsync -ar src/* GpsMaster_jar/ --exclude=*.java &&
JVER=$(javac -version 2>&1 ) &&
# Check the java version
# Oracle Java 1.8 has been tested
case $JVER in
  javac*1.8.*)
    JDKVER=JDK7-12
  ;;
  *)
  echo >&2 "untested java version $JVER"
  echo >&2 "See createJAR.sh"
  exit 1
  ;;
esac
VERSION_STRING="public static final String VERSION_NUMBER ="

VERSION_NUMBER=$(git grep "$VERSION_STRING" | sed -e "s/^.*://g" -e "s/$VERSION_STRING//g")
VERSION_NUMBER=$(echo $VERSION_NUMBER | sed -e "s/[^.0-9]//g" -e "s/ //g")
echo VERSION_NUMBER=$VERSION_NUMBER
rsync -ar META-INF/ GpsMaster_jar/ &&
( cd GpsMaster_jar/ &&
    jar cmf MANIFEST.MF GpsMaster_$VERSION_NUMBER.$JDKVER.jar *
)
ls -l GpsMaster_jar/GpsMaster_$VERSION_NUMBER.$JDKVER.jar
