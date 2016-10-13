package com.tomtom.sosnicki.okienczak.rest.dto;

public final class OptimalRouteRequest {

    private double latitude;
    private double longitude;
    private Double usedThreshold;

    public OptimalRouteRequest() {
    }

    public OptimalRouteRequest(double latitude, double longitude, Double usedThreshold) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.usedThreshold = usedThreshold;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getUsedThreshold() {
        return usedThreshold;
    }
}
