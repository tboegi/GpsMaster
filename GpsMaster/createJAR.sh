#!/bin/sh
rm -rf GpsMaster_jar
mkdir -p GpsMaster_jar &&
rsync -ar external/ GpsMaster_jar/ &&
rsync -ar src/* GpsMaster_jar/ --exclude=*.java &&
rsync -ar META-INF/ GpsMaster_jar/ &&
( cd GpsMaster_jar/ &&
		jar cmf MANIFEST.MF GpsMaster_0_63.31.jar *
)
