#!/bin/sh

CLASSES=$(ls ./external/*.jar)
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')

JVER=$(javac -version 2>&1)

# Check the java version
# Oracle Java 1.8 has been tested as well as Java 10
case $JVER in
  javac*1.8.*)
  ;;
  javac*10.*.*)
	CLASSES="${CLASSES}:./external/javax/xml/bind/jaxb-api/2.3.0/jaxb-api-2.3.0.jar"
  ;;
  javac*11.*.*)
	CLASSES="${CLASSES}:./external/javax/xml/bind/jaxb-api/2.3.0/jaxb-api-2.3.0.jar"
  ;;
  javac*13.*.*)
	CLASSES="${CLASSES}:./external/javax/xml/bind/jaxb-api/2.3.0/jaxb-api-2.3.0.jar"
  ;;
  *)
  echo >&2 "untested java version $JVER"
  echo >&2 "See compile.sh"
  exit 1
  ;;
esac

CLASSES="-cp $CLASSES"
echo CLASSES=$CLASSES | tr ':' '\012'

JAVAC=javac
export JAVAC &&
SRC=$(git ls-files "*.java"  ) &&
export JOPTS &&
CLASS=$(find src -name "*.class") &&
if test "$CLASS"; then
  rm -rf $CLASS
fi &&
cmd=$(echo $JAVAC "$JOPTS" "$CLASSES" "$SRC") &&
echo cmd=$cmd &&
eval $cmd
