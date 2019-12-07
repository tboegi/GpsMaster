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
    JDKVER=JDK7-13
  ;;
  *)
  echo >&2 "untested java version $JVER"
  echo >&2 "See createJAR.sh"
  exit 1
  ;;
esac
VERSION_STRING="public static final String VERSION_NUMBER ="

# Extract the VERSION string from java source code, remove everything until the first digit
# giving a result like this '0.63.33-rc0";' after the first sed
# Remove the '"' abd ';' in the second sed expression
VERSION_NUMBER=$(git grep "$VERSION_STRING" "*.java" | sed -e "s/^.*VERSION_NUMBER =[^0-9]*//g" -e s/[^-.0-9a-zA-Z]//g)
echo VERSION_NUMBER=$VERSION_NUMBER
rsync -ar META-INF/ GpsMaster_jar/ &&
( cd GpsMaster_jar/ &&
    jar cmf MANIFEST.MF GpsMaster_$VERSION_NUMBER.$JDKVER.jar *
)
ls -l GpsMaster_jar/GpsMaster_$VERSION_NUMBER.$JDKVER.jar
