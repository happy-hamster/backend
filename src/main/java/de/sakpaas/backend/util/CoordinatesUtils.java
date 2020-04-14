package de.sakpaas.backend.util;

public class CoordinatesUtils {

  /**
   * Calculates the distance between two given coordinates.
   *
   * @param lat1 Latitude of the first coordinate
   * @param lon1 Longitude of the first coordinate
   * @param lat2 Latitude of the second coordinate
   * @param lon2 Longitude of the second coordinate
   * @return Distance between the two coordinates in kilometres
   */
  public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
    // https://www.daniel-braun.com/technik/distanz-zwischen-zwei-gps-koordinaten-in-java-berchenen/
    int radius = 6371;
    double lat = Math.toRadians(lat2 - lat1);
    double lon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(lat / 2) * Math.sin(lat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lon / 2) * Math.sin(lon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = radius * c;
    return Math.abs(d);
  }
}
