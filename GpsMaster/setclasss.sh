#!/bin/sh
CLASSES=$(ls ./external/*.jar GpsMaster_jar/GpsMaster_XX.YY.jar)
if test -d GpsMaster_jar; then
	CLASSES="$CLASSES $(ls GpsMaster_jar/GpsMaster_XX.YY.jar)"
fi
CLASSES=$(echo $CLASSES | sed -e 's/ /:/g')
echo JOPTS="-cp $CLASSES"
