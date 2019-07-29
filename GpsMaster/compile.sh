#!/bin/sh

CLASSES=$(ls ./external/*.jar) 
echo CLASSES=$CLASSES
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')
echo CLASSES=$CLASSES
CLASSES="-cp $CLASSES"
echo CLASSES=$CLASSES

#JVER=$(javac -version 2>&1 )
#if ! $(echo $JVER | grep -q "javac 1.8.0"); then
#	echo >&2 "wrong javac version $JVER (must be 1.8.0)"
#  exit 1
#fi

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
