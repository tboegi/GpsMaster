#!/bin/sh

CLASSES=$(ls ./external/*.jar)
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')
CLASSES="-cp $CLASSES"

JVER=$(javac -version 2>&1)

# Check the java version
# Oracle Java 1.8 has been tested as well as Openjdk 11.0
case $JVER in
  javac*1.8.*)
  ;;
  *)
  echo >&2 "untested java version $JVER"
  echo >&2 "See compile.sh"
  exit 1
  ;;
esac

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
