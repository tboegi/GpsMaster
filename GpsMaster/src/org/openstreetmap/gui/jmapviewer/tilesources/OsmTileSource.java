// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import java.io.IOException;
import java.io.File;

/**
 * OSM Tile source.
 */
public class OsmTileSource {

    private static String filePathCache = null;
    private static boolean debug = false;
    static boolean initDone = false;
    private static String getFilePathCache() {
        if (!initDone) {
            String home = System.getenv("HOME");
            if (home == null) {
                String homeDrive = System.getenv("HOMEDRIVE");
                String homePath = System.getenv("HOMEPATH");
                home = homeDrive + homePath;
            }
            if (home != null) {
                filePathCache = home + File.separator + ".cache";
            }
            initDone = true;
        }
        return filePathCache;
    }
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
            super("OSM Online", PATTERN, "MAPNIK");
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

        /**
         * Constructs a new {@code "MapnikDiskCache"} tile source.
         */
        public MapnikDiskCache() {
            super("OSM DiskCache", PATTERN, "MAPNIKDISKCACHE");
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
            String cachePath = null;
            try {
                tilePath = getTilePath(zoom, tilex, tiley);
            } catch (IOException e) {
                System.err.println("Mapnik.getTilePath() "+e.getMessage());
                return null;
            }
            cachePath = getFilePathCache();
            if (cachePath == null) return null;

            String fileName = cachePath + File.separator + "openstreetmap" + tilePath;

            if (debug) System.out.println("Mapnik.tilePath=" + tilePath);
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
    /* The following are not Open Street Map provoders as such.
       They follow the same principle, so I put them here */

    /**
     * The "HikeAndBike Map" tile source.
     */
    public static class HikeAndBikeMap extends AbstractOsmTileSource {
        private static final String PATTERN = "https://%s.tiles.wmflabs.org/hikebike";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum;

        /**
         * Constructs a new {@code HikeAndBikeMap} tile source.
         */
        public HikeAndBikeMap() {
            super("Hike & Bike Map", PATTERN, "hikeandbikemap");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    /**
     * The "OpenTopo Map" tile source.
     */
    public static class OpenTopoMap extends AbstractOsmTileSource {
        private static final String PATTERN = "https://%s.tile.opentopomap.org";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum;

        /**
         * Constructs a new {@code OpenTopoMap} tile source.
         */
        public OpenTopoMap() {
            super("OpenTopoMap", PATTERN, "opentopomap");
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
        @Override
        public int getMaxZoom() {
            return 17;
        }
    }
}
