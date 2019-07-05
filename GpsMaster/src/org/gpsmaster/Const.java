package org.gpsmaster;

/**
 * Class containing application-global constant definitions
 *
 * @author rfu
 *
 */
public final class Const {

	/* GPX Extensions */
	public static final String EXT_ACTIVITY = "gpsm:activity";
    public static final String EXT_COLOR = "gpsm:color";
    public static final String EXT_FILE = "gpsm:file";

    /* API Keys  */
    public static final String MAPQUEST_API_KEY="Fmjtd%7Cluub2lu12u%2Ca2%3Do5-96y5qz";
    public static final String GPSIES_API_KEY = "wbqmlgbborqsbtqx";

    /* Icon path */
    public static final String ICONPATH = "/org/gpsmaster/icons/";
    public static final String ICONPATH_MARKER = ICONPATH + "marker/";
    public static final String ICONPATH_DIALOGS = ICONPATH + "dialogs/";

    /* Format strings */
    public static final String FMT_DIST = "%.2f";
    public static final String FMT_SPEED = "%.1f";
    public static final String FMT_ELE = "%.0f";
    public static final String FMT_ELESPEED = "%.0f";

    /* Property Change Events */
	public static final String PCE_NEWGPX = "newGpx"; // add new GPXFile
	public static final String PCE_REFRESHGPX = "refreshGpx"; // refresh active GpxObject
	public static final String PCE_ACTIVEGPX = "activeGpxObject"; // set active GpxObject
	public static final String PCE_ACTIVEWPT = "activeWpt"; // set active waypoint
	public static final String PCE_REPAINTMAP = "repaintMap"; // repaint map on mapPanel
	public static final String PCE_ADDMARKER = "addMarker";
	public static final String PCE_REMOVEMARKER = "removeMarker";
    // public static final String PCE_SEARCH = "search";
	public static final String PCE_CENTERMAP = "centerMap"; // set the center of the map


}
