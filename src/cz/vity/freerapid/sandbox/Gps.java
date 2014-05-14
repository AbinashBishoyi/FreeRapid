package cz.vity.freerapid.sandbox;

/**
 * @author Vity
 */
public class Gps {

    //    private final static double EarthRadius = 6378137;
    private final static double MinLatitude = -85.05112878;
    private final static double MaxLatitude = 85.05112878;
    private final static double MinLongitude = -180;
    private final static double MaxLongitude = 180;


    static double[] getGPS(int px, int py) {
        double lot, lat;
        double sever = (((double) py) / 32) + 1300000;
        double vychod = (((double) px) / 32) - 3700000;

        double pi = Math.PI;
        double units = 1.0;
        double k = 0.9996;
        double a = 6378137.0;
        double f = 1 / 298.257223563;
        double b = a * (1 - f);
        double e2 = (a * a - b * b) / (a * a);
        double e = Math.sqrt(e2);
        double ei2 = (a * a - b * b) / (b * b);
        double ei = Math.sqrt(ei2);
        double n = (a - b) / (a + b);
        double G = a * (1.0 - n) * (1.0 - n * n) * (1.0 + (9.0 / 4.0) * n * n + (255.0 / 64.0) * Math.pow(n, 4)) * (pi / 180.0);
        double north = (sever - 0) * units;
        double east = (vychod - 500000.0) * units;
        double m = north / k;
        double sigma = (m * pi) / (180.0 * G);
        double footlat = sigma + ((3.0 * n / 2.0) - (27.0 * Math.pow(n, 3) / 32.0)) * Math.sin(2.0 * sigma) + ((21.0 * n * n / 16.0) - (55.0 * Math.pow(n, 4) / 32.0)) * Math.sin(4 * sigma) + (151.0 * Math.pow(n, 3) / 96.0) * Math.sin(6.0 * sigma) + (1097.0 * Math.pow(n, 4) / 512.0) * Math.sin(8.0 * sigma);
        double rho = a * (1.0 - e2) / Math.pow(1.0 - (e2 * Math.sin(footlat) * Math.sin(footlat)), (3.0 / 2.0));
        double nu = a / Math.sqrt(1 - (e2 * Math.sin(footlat) * Math.sin(footlat)));
        double psi = nu / rho;
        double t = Math.tan(footlat);
        double x = east / (k * nu);
        double laterm1 = (t / (k * rho)) * (east * x / 2.0);
        double laterm2 = (t / (k * rho)) * (east * Math.pow(x, 3) / 24.0) * (-4.0 * psi * psi + 9.0 * psi * (1.0 - t * t) + 12.0 * t * t);
        double laterm3 = (t / (k * rho)) * (east * Math.pow(x, 5) / 720.0) * (8.0 * Math.pow(psi, 4) * (11.0 - 24.0 * t * t) - 12.0 * Math.pow(psi, 3) * (21.0 - 71.0 * t * t) + 15.0 * psi * psi * (15.0 - 98.0 * t * t + 15.0 * Math.pow(t, 4)) + 180.0 * psi * (5.0 * t * t - 3.0 * Math.pow(t, 4)) + 360.0 * Math.pow(t, 4));
        double laterm4 = (t / (k * rho)) * (east * Math.pow(x, 7) / 40320.0) * (1385.0 + 3633.0 * t * t + 4095.0 * Math.pow(t, 4) + 1575.0 * Math.pow(t, 6));
        double latrad = footlat - laterm1 + laterm2 - laterm3 + laterm4;

        lat = Math.toDegrees(latrad);

        double seclat = 1 / Math.cos(footlat);
        double loterm1 = x * seclat;
        double loterm2 = (Math.pow(x, 3) / 6.0) * seclat * (psi + 2.0 * t * t);
        double loterm3 = (Math.pow(x, 5) / 120.0) * seclat * (-4.0 * Math.pow(psi, 3) * (1.0 - 6.0 * t * t) + psi * psi * (9.0 - 68.0 * t * t) + 72.0 * psi * t * t + 24.0 * Math.pow(t, 4));
        double loterm4 = (Math.pow(x, 7) / 5040.0) * seclat * (61.0 + 662.0 * t * t + 1320.0 * Math.pow(t, 4) + 720.0 * Math.pow(t, 6));
        double w = loterm1 - loterm2 + loterm3 - loterm4;
        double longrad = Math.toRadians(15.0) + w;
        lot = Math.toDegrees(longrad);
        return new double[]{lot, lat};
    }

    /// Converts a point from latitude/longitude WGS-84 coordinates (in degrees)
    /// into pixel XY coordinates at a specified level of detail.
    /// </summary>
    /// <param name="latitude">Latitude of the point, in degrees.</param>
    /// <param name="longitude">Longitude of the point, in degrees.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="pixelX">Output parameter receiving the X coordinate in pixels.</param>
    /// <param name="pixelY">Output parameter receiving the Y coordinate in pixels.</param>
    public static int[] longLatToPixelXY(double longitude, double latitude, int levelOfDetail) {
        int pixelX, pixelY;
        latitude = clip(latitude, MinLatitude, MaxLatitude);
        longitude = clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        int mapSize = mapSize(levelOfDetail);
        pixelX = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        pixelY = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);
        return new int[]{pixelX, pixelY};
    }


    /// <summary>
    /// Clips a number to the specified minimum and maximum values.
    /// </summary>
    /// <param name="n">The number to clip.</param>
    /// <param name="minValue">Minimum allowable value.</param>
    /// <param name="maxValue">Maximum allowable value.</param>
    /// <returns>The clipped value.</returns>
    private static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }


    /// <summary>
    /// Determines the map width and height (in pixels) at a specified level
    /// of detail.
    /// </summary>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <returns>The map width and height in pixels.</returns>
    public static int mapSize(int levelOfDetail) {
        return 256 << levelOfDetail;
    }


    /// <summary>
    /// Converts a pixel from pixel XY coordinates at a specified level of detail
    /// into latitude/longitude WGS-84 coordinates (in degrees).
    /// </summary>
    /// <param name="pixelX">X coordinate of the point, in pixels.</param>
    /// <param name="pixelY">Y coordinates of the point, in pixels.</param>
    /// <param name="levelOfDetail">Level of detail, from 1 (lowest detail)
    /// to 23 (highest detail).</param>
    /// <param name="latitude">Output parameter receiving the latitude in degrees.</param>
    /// <param name="longitude">Output parameter receiving the longitude in degrees.</param>
    public static double[] pixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail) {
        double latitude;
        double longitude;
        double mapSize = mapSize(levelOfDetail);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        longitude = 360 * x;
        return new double[]{longitude, latitude};
    }


    public static void main(String[] args) {

//        final double[] gps = getGPS(133168016, 135977728);
//        final String s1 = Arrays.toString(gps);
//        System.out.println("s1 = " + s1);
//        final int[] ints = longLatToPixelXY(gps[0], gps[1], 16);
//        ints[0] = ints[0] / 256;
//        ints[1] = ints[1] / 256;
//        final String s = Arrays.toString(ints);
//        System.out.println("s = " + s);


    }

}
