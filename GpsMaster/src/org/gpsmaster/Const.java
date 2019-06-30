package org.gpsmaster;

import java.awt.Color;

/**
 * Class containing application-global constant definitions
 * 
 * @author rfu
 *
 */
public final class Const {

	/* GPX Extensions */
	public static final String EXT_GPSM_PRE = "gpsm";
	public static final String EXT_ACTIVITY = EXT_GPSM_PRE + ":activity";
    public static final String EXT_COLOR = EXT_GPSM_PRE + ":color";
    public static final String EXT_FILE = EXT_GPSM_PRE + ":file";
    public static final String EXT_TYPE = EXT_GPSM_PRE + ":type";
    public static final String EXT_MARKER = EXT_GPSM_PRE + ":marker"; // fully qualified marker class name
    public static final String EXT_HEADING = "heading";
    public static final String EXT_SPEED = "speed";
    public static final String EXT_GPSIESURL = EXT_GPSM_PRE + ":gpsiesurl";
    
    public static final String EXT_NMEA_PRE = "nmea";
    
	public static final String EXT_HRM = "hrm"; // HRM sourceFmt key prefix
    public static final String EXT_HRMCAL = EXT_HRM + ":calories";
    public static final String EXT_HRMHR = EXT_HRM + ":hr";
    public static final String EXT_HRMSPEED = EXT_HRM + ":speed";
    public static final String EXT_HRMCADENCE = EXT_HRM + ":cadence";
    public static final String EXT_HRMPOWER = EXT_HRM + ":power";
    public static final String EXT_HRMTEMP = EXT_HRM + ":temp";
    
    /* Colors */
    public static final Color TRANSPARENTWHITE = new Color(255, 255, 255, 192);
    
    /* API Keys  */
    public static final String MAPQUEST_API_KEY="1bAcGKAeLtJCNkwIbgo7kZrM0D48Dcjg";
    public static final String GPSIES_API_KEY = "wbqmlgbborqsbtqx";
    public static final String GRAPHHOPPER_API_KEY="84c6481b-ab01-4307-93e1-925c67145a62";
    public static final String THUNDERFOREST_API_KEY="dccb3c4fe3ed482a9ecd30a816bec336";
	
    /* Icon path */
    public static final String ICONPATH = "/org/gpsmaster/icons/";
    public static final String ICONPATH_MARKER = ICONPATH + "marker/";
    public static final String ICONPATH_DIALOGS = ICONPATH + "dialogs/";
    public static final String ICONPATH_MENUBAR = ICONPATH + "menubar/";
    public static final String ICONPATH_DLBAR = ICONPATH + "downloadbar/";
    public static final String ICONPATH_TOOLBAR = ICONPATH + "toolbar/";
    public static final String ICONPATH_ACTIVITIES = ICONPATH + "activities/";
    public static final String ICONPATH_CHART = ICONPATH + "chart/";
    public static final String ICONPATH_TREE = ICONPATH + "tree/";
    
    /* Format strings */
    public static final String FMT_DIST = "%.2f";
    public static final String FMT_SPEED = "%.1f";
    public static final String FMT_ELE = "%.0f";
    public static final String FMT_ELESPEED = "%.0f";
    public static final String SDF_STANDARD = "yyyy-MM-dd HH:mm:ss z"; 
    public static final String SDF_DATETIME = "yyyy-MM-dd HH:mm:ss";
    
    /* Property Change Events */
	public static final String PCE_NEWGPX = "newGpx"; // add new GPXFile
	public static final String PCE_REFRESHGPX = "refreshGpx"; // refresh active GpxObject
	public static final String PCE_REMOVEGPX = "removeGpx"; // remove active GpxObject
	public static final String PCE_ACTIVEGPX = "activeGpxObject"; // set active GpxObject
	public static final String PCE_ACTIVE_TRKPT = "activeTrkPt"; // set active trackpoint
	public static final String PCE_ACTIVE_WPT = "activeWpt"; // set active wayopoint (marker)
	public static final String PCE_REPAINTMAP = "repaintMap"; // repaint map on mapPanel
	public static final String PCE_ADDMARKER = "addMarker";
	public static final String PCE_ADDROUTEPT = "addRoutept"; // add point to route
	public static final String PCE_REMOVEMARKER = "removeMarker";
	public static final String PCE_REFRESHDB = "refreshDb";
	public static final String PCE_TOTRACK = "toTrack";
	public static final String PCE_TOROUTE = "toRoute";
	public static final String PCE_ADDROUTE = "addRoute";
	public static final String PCE_ELEFINISHED = "eleFinished";
	public static final String PCE_TRANSFERSTARTED = "transferStarted";
	public static final String PCE_TRANSFERFINISHED = "transferFinished";
	public static final String PCE_TRANSFERITEMSTATECHANGED = "transferItemStateChanged";
	public static final String PCE_LOGCHANGED = "logChanged";
	public static final String PCE_CENTERMAP = "centerMap"; // set the center of the map				
	public static final String PCE_CANCELLED = "cancelled";
	public static final String PCE_UNDO = "undo"; // undo stack has been modified
	
	public static final String TAG_EXTENSIONS = "extensions"; // XML tag name for <extensions> section
	
}
