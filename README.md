# GpsMaster
This repo has the source code from which

http://www.gpsmaster.org/download/GpsMaster_0.63.31.jar

had been build from the branch master-0.63.31.

This branch includes the sources of the older versions.

The GpsMaster_0.63.31.jar was build using the Makefile -
How to build with e.g. eclipse - I don't know

GpsMaster_0.63.32.jar is build with Makefile using Java 1.8.
The final jar includes the javax/xml classes and should run under
Java 8,10,11 (I don't have Java 9 to test)

In any case a collection of external jar files is found under the
"external" directory.

They are part of the final .jar file.

For new contributions, the dev branch should be used and pull requests
can be made.

If you have Java 11, maven can be used to build a jar file for Java 11.
This is how I created the jar file on a Debian machine:

`$ cd GpsMaster`

`$ mvn package`

And then I end up with something like this:

`# Building jar: .../target/GpsMaster-jar-with-dependencies.jar`

After that, rename
GpsMaster-jar-with-dependencies.jar
into
GpsMaster_0.63.37.JDK11-and-higher.jar
