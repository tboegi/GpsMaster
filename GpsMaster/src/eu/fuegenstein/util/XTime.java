package eu.fuegenstein.util;

public class XTime {

    /**
     * Convert duration to human readable text 
     * @param duration in seconds
     * @return String containing duration a la "2hr 10min 26sec" 
     */
    public static String getDurationText(long duration) {
    	// TODO redundant code
        
        long seconds = duration % 60;
		long minutes = duration / 60 % 60;
		long hours = duration / (60 * 60) % 24;
		long days = duration / (24 * 60 * 60);
		
		String timeString = String.format("%dhr %02dmin %02dsec", hours, minutes, seconds); 						   
		if (days > 0) {
			 timeString = String.format("%dd ", days).concat(timeString);  								
		}
		return timeString;
    }

  /**
  * Convert duration to human readable text 
  * @param duration in seconds
  * @return String containing duration a la "02:10:26" 
  */
 public static String getDurationString(long duration) {
 	// TODO redundant code
     
     long seconds = duration % 60;
		long minutes = duration / 60 % 60;
		long hours = duration / (60 * 60) % 24;
		long days = duration / (24 * 60 * 60);
		
		String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds); 						   
		if (days > 0) {
			 timeString = String.format("%dd ", days).concat(timeString);  								
		}
		return timeString;
 }
}
