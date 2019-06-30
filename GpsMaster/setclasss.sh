#!/bin/sh
if test -d GpsMaster_jar; then
	CLASSES="$CLASSES $(ls GpsMaster_jar/GpsMaster_0_63.31.jar)"
else
	CLASSES=$(ls ./external/*.jar)
fi
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')
echo JOPTS="-cp $CLASSES"
