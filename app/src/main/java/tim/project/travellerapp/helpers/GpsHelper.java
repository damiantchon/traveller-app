package tim.project.travellerapp.helpers;

public class GpsHelper {
    public static double[] decodeStringGps(String gps) {
        String[] decoded = gps.split(",");
        return new double[]{Double.parseDouble(decoded[0]), Double.parseDouble(decoded[1])};
    }

    public static String encodeDoubleLatLng(double latitude, double longitude) {
        String gps = "";
        gps = gps.concat(String.valueOf(latitude)).concat(",").concat(String.valueOf(longitude));

        return gps;
    }

    public static String encodeStringLatLng(String latitude, String longitude) {
        String gps = "";
        latitude = latitude.trim();
        longitude = longitude.trim();

        gps = gps.concat(latitude).concat(",").concat(longitude);

        return gps;
    }
}
