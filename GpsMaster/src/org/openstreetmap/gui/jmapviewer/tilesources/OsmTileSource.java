// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import java.io.IOException;
import java.io.File;

/**
 * OSM Tile source.
 */
public class OsmTileSource {

    private static boolean debug = false;
    /**
     * The default "Mapnik" OSM tile source.
     */
    public static class Mapnik extends AbstractOsmTileSource {

        private static final String PATTERN = "https://%s.tile.openstreetmap.org";

        private static final String[] SERVER = {"a", "b", "c"};

        private int serverNum;

        /**
         * Constructs a new {@code "Mapnik"} tile source.
         */
        public Mapnik() {
            super("Mapnik", PATTERN, "MAPNIK");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }

        @Override
        public String getCachedFilePath(int zoom, int tilex, int tiley) {
            return null;
        }
    }

    /**
     * The default "Mapnik" OSM tile source with disk cache
     */
    public static class MapnikDiskCache extends AbstractOsmTileSource {

        private static final String PATTERN = "https://%s.tile.openstreetmap.org";

        private static final String[] SERVER = {"a", "b", "c"};

        private int serverNum;

        private String fileBasePath = null;
        /**
         * Constructs a new {@code "MapnikDiskCache"} tile source.
         */
        public MapnikDiskCache() {
            super("MapnikDiskCache", PATTERN, "MAPNIKDISKCACHE");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }

        @Override
        public String getCachedFilePath(int zoom, int tilex, int tiley) {
            String tilePath = null;
            try {
                tilePath = getTilePath(zoom, tilex, tiley);
            } catch (IOException e) {
                System.err.println("Mapnik.getTilePath() "+e.getMessage());
                return null;
            }
            if (fileBasePath == null) {
                String home = System.getenv("HOME");
                if (home == null) {
                    String homeDrive = System.getenv("HOMEDRIVE");
                    String homePath = System.getenv("HOMEPATH");
                    home = homeDrive + homePath;
                }
                if (home != null) {
                    fileBasePath = home + File.separator +
                        ".cache" + File.separator + "openstreetmap";
                }
            }
            String fileName = fileBasePath + tilePath;
            //File candidate = new File(basePath, enc);
            //candidate.getParentFile().mkdirs();

            if (debug) System.out.println("Mapnik.tilePath: =" + tilePath);
            /* Not yet implemented */
            if (debug) System.out.println("Mapnik.getCachedFilePath=" + fileName);
            return fileName;
        }
    }

    /**
     * The "Cycle Map" OSM tile source.
     */
    public static class CycleMap extends AbstractOsmTileSource {

        private static final String PATTERN = "http://%s.tile.opencyclemap.org/cycle";

        private static final String[] SERVER = {"a", "b", "c"};

        private int serverNum;

        /**
         * Constructs a new {@code CycleMap} tile source.
         */
        public CycleMap() {
            super("Cyclemap", PATTERN, "opencyclemap");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }

        @Override
        public int getMaxZoom() {
            return 18;
        }
    }
}
