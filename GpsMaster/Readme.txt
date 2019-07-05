JMapViewer

(c) 2007, Tim Haussmann
(c) 2008-2012, Jan Peter Stotz
(c) 2009-2016, Dirk Stöcker
(c) 2009, Stefan Zeller
(c) 2009, Karl Guggisberg
(c) 2009, Dave Hansen
(c) 2010-2011, Ian Dees
(c) 2010-2011, Michael Vigovsky
(c) 2011-2016, Paul Hartmann
(c) 2011-2016, Gleb Smirnoff
(c) 2011-2016, Vincent Privat
(c) 2011, Jason Huntley
(c) 2012-2016, Simon Legner
(c) 2012, Teemu Koskinen
(c) 2012, Jiri Klement
(c) 2013, Matt Hoover
(c) 2013, Alexei Kasatkin
(c) 2013, Galo Higueras
(c) 2015-2016, Wiktor Niesiobędzki

This work bases partly on the JOSM plugin "Slippy Map Chooser" by Tim Haussmann

License: GPL

FAQ:

1. What is JMapViewer?

JMapViewer is a Java Swing component for integrating OSM maps in to your Java 
application. JMapViewer allows you to set markers on the map or zoom to a specific 
location on the map.

2. How does JMapViewer work?

JMapViewer loads bitmap tiles from the OpenStreetmap tile server (Mapnik renderer).
Therefore any application using JMapViewer requires a working Internet connection.    

3. How do I use JMapViewer in my application?

You can just create an instance of the class org.openstreetmap.gui.jmapviewer.JMapViewer
using the default constructor and add it to your panel/frame/windows.
For more details please see the Demo class in the same package.
