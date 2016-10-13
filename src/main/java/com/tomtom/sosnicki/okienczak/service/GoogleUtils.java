package com.tomtom.sosnicki.okienczak.service;

import java.util.ArrayList;
import java.util.List;

public final class GoogleUtils {
    public static double[][] decodePolyline(String encoded) {
        List<double[]> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            double[] point = {(double) lat / 1E5, (double) lng / 1E5};
            polyline.add(point);
        }
        return polyline.toArray(new double[0][]);
    }
}
