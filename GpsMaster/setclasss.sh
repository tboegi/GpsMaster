#!/bin/sh
CLASSES=$(ls ./external/*.jar)
if test -d GpsMaster_jar; then
	CLASSES="$CLASSES $(ls GpsMaster_jar/GpsMaster_*.jar)"
fi
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')
echo JOPTS="-cp $CLASSES"
