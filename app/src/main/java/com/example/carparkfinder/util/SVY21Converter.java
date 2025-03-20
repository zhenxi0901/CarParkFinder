package com.example.carparkfinder.util;

/**
 * Converts SVY21 (Singapore’s coordinate system) to WGS84 (Latitude/Longitude).
 *
 * Formula reference from Singapore Land Authority (SLA).
 */
public class SVY21Converter {
    private static final double a = 6378137.0;  // Semi-major axis of WGS84 ellipsoid
    private static final double f = 1 / 298.257223563;  // Flattening of WGS84 ellipsoid
    private static final double e2 = 2 * f - f * f;  // Square of eccentricity

    // SVY21 Projection Parameters
    private static final double k0 = 1.0; // Scale factor
    private static final double lon0 = Math.toRadians(103.833333333); // Longitude of origin in radians (103° 50' E)
    private static final double lat0 = Math.toRadians(1.366666667); // Latitude of origin in radians (1° 22' N)
    private static final double falseEasting = 28001.642; // False Easting
    private static final double falseNorthing = 38744.572; // False Northing

    /**
     * Converts SVY21 (X, Y) coordinates to WGS84 (Latitude, Longitude).
     *
     * @param easting  SVY21 Easting (X)
     * @param northing SVY21 Northing (Y)
     * @return double[] {latitude, longitude}
     */
    public static double[] svy21ToWgs84(double easting, double northing) {
        double Nprime = northing - falseNorthing;
        double Mo = getMeridionalArc(lat0);
        double Mprime = Mo + (Nprime / k0);
        double footLat = getFootLatitude(Mprime);

        double sinFootLat = Math.sin(footLat);
        double cosFootLat = Math.cos(footLat);
        double tanFootLat = Math.tan(footLat);

        double esqSin = e2 * sinFootLat * sinFootLat;
        double v = a / Math.sqrt(1 - esqSin);
        double p = a * (1 - e2) / Math.pow(1 - esqSin, 1.5);
        double n = v / p;
        double T = tanFootLat * tanFootLat;
        double C = e2 * cosFootLat * cosFootLat / (1 - e2);
        double A = (easting - falseEasting) / k0 / v;

        double latRad = footLat - (v * tanFootLat / p) * (
                (A * A) / 2 -
                        (5 + 3 * T + 10 * C - 4 * C * C - 9 * e2) * Math.pow(A, 4) / 24 +
                        (61 + 90 * T + 298 * C + 45 * T * T - 252 * e2 - 3 * C * C) * Math.pow(A, 6) / 720
        );

        double lonRad = lon0 + (
                A -
                        (1 + 2 * T + C) * Math.pow(A, 3) / 6 +
                        (5 - 2 * C + 28 * T - 3 * C * C + 8 * e2 + 24 * T * T) * Math.pow(A, 5) / 120
        ) / cosFootLat;

        double latitude = Math.toDegrees(latRad);
        double longitude = Math.toDegrees(lonRad);
        return new double[]{latitude, longitude};
    }

    /**
     * Computes the meridional arc length for a given latitude.
     */
    private static double getMeridionalArc(double lat) {
        double n = (a - (a * (1 - f))) / (a + (a * (1 - f)));
        double A0 = a * (1 - n + (5 * (n * n - n * n * n)) / 4 + (81 * (n * n * n * n - n * n * n * n * n)) / 64);
        double B0 = (3 * a * (n - n * n + (7 * (n * n * n - n * n * n * n)) / 8 + (55 * (n * n * n * n * n)) / 64)) / 2;
        double C0 = (15 * a * (n * n - n * n * n + (3 * (n * n * n * n - n * n * n * n * n)) / 4)) / 16;
        double D0 = (35 * a * (n * n * n - n * n * n * n)) / 48;
        double E0 = (315 * a * (n * n * n * n - n * n * n * n * n)) / 512;
        return A0 * lat - B0 * Math.sin(2 * lat) + C0 * Math.sin(4 * lat) - D0 * Math.sin(6 * lat) + E0 * Math.sin(8 * lat);
    }

    /**
     * Uses an iterative method to find the foot latitude given meridional arc length.
     */
    private static double getFootLatitude(double Mprime) {
        double lat = Mprime / (a * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));
        double M;
        do {
            M = getMeridionalArc(lat);
            lat += (Mprime - M) / (a * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));
        } while (Math.abs(Mprime - M) > 1e-4);
        return lat;
    }
}
