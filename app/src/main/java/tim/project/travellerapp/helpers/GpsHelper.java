package tim.project.travellerapp.helpers;

public class GpsHelper {
    public static double[] decodeStringGps(String gps) {
        String[] decoded = gps.split(",");
        return new double[]{Double.parseDouble(decoded[0]), Double.parseDouble(decoded[1])};
    }
}
