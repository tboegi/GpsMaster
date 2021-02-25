package org.gpsmaster.gpsloader;

import java.util.HashMap;


/*
 * Class holding Key/Value pairs for loader-specific configuration
 */
public class LoaderConfig {

    private String className = "";
    private HashMap<String, String> config = new HashMap<String, String>();
    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return the config
     */
    public HashMap<String, String> getMap() {
        return config;
    }

}
