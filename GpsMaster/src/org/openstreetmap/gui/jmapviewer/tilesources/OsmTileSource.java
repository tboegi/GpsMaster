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
        public Mapnik(String name, String id) {
            super(name, PATTERN, id);
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
    public static class MapnikDiskCache extends Mapnik  {
        public MapnikDiskCache() {
            super("OSM DiskCache", "MAPNIKDISKCACHE");
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

        @Override
        public long getCacheRefreshThresholdDays() {
            return -1;
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
            super("Cyclemap Online", PATTERN, "opencyclemap");
        }
        public CycleMap(String name, String id) {
            super(name, PATTERN, id);
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

    public static class CycleMapDiskCache extends CycleMap  {
        public CycleMapDiskCache() {
            super("CycleMap", "CYCLEMAPDISKCACHE");
        }

        @Override
        public String getCachedFilePath(int zoom, int tilex, int tiley) {
            String tilePath = null;
            String cachePath = null;
            try {
                tilePath = getTilePath(zoom, tilex, tiley);
            } catch (IOException e) {
                System.err.println("CycleMap.getTilePath() "+e.getMessage());
                return null;
            }
            cachePath = getFilePathCache();
            if (cachePath == null) return null;

            String fileName = cachePath + File.separator + "cyclemap" + tilePath;

            if (debug) System.out.println("CycleMap.tilePath=" + tilePath);
            if (debug) System.out.println("CycleMap.getCachedFilePath=" + fileName);
            return fileName;
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
            super("Hike&Bike Online", PATTERN, "hikeandbikemap");
        }
        public HikeAndBikeMap(String name, String id) {
            super(name, PATTERN, id);
        }

        @Override
        public String getBaseUrl() {
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    public static class HikeAndBikeMapDiskCache extends HikeAndBikeMap  {
        public HikeAndBikeMapDiskCache() {
            super("Hike&Bike", "HIKEANDBIKDISKCACHE");
        }

        @Override
        public String getCachedFilePath(int zoom, int tilex, int tiley) {
            String tilePath = null;
            String cachePath = null;
            try {
                tilePath = getTilePath(zoom, tilex, tiley);
            } catch (IOException e) {
                System.err.println("HikeAndBikeMap.getTilePath() "+e.getMessage());
                return null;
            }
            cachePath = getFilePathCache();
            if (cachePath == null) return null;

            String fileName = cachePath + File.separator + "hikeandbike" + tilePath;

            if (debug) System.out.println("HikeAndBikeMap.tilePath=" + tilePath);
            if (debug) System.out.println("HikeAndBikeMap.getCachedFilePath=" + fileName);
            return fileName;
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
            super("OpenTopoMap Online", PATTERN, "opentopomap");
        }
        public OpenTopoMap(String name, String id) {
            super(name, PATTERN, id);
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
    public static class OpenTopoMapDiskCache extends OpenTopoMap  {
        public OpenTopoMapDiskCache() {
            super("OpenTopoMap", "OPENTOPOMAP");
        }

        @Override
        public String getCachedFilePath(int zoom, int tilex, int tiley) {
            String tilePath = null;
            String cachePath = null;
            try {
                tilePath = getTilePath(zoom, tilex, tiley);
            } catch (IOException e) {
                System.err.println("OpentopoMap.getTilePath() "+e.getMessage());
                return null;
            }
            cachePath = getFilePathCache();
            if (cachePath == null) return null;

            String fileName = cachePath + File.separator + "opentopmap" + tilePath;

            if (debug) System.out.println("OpentopoMap.tilePath=" + tilePath);
            if (debug) System.out.println("OpentopoMap.getCachedFilePath=" + fileName);
            return fileName;
        }
    }
}
