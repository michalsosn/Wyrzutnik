package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.RouteEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.validation.constraints.AssertTrue;

public final class RouteDto implements Dto<RouteEntity> {

    private static final int MAX_LENGTH = 20;
    private static final int MAX_SUBLENGTH = 100;

    private Long id;
    private double[][] lineString;
    private Long lengthInMeters;
    private Long travelTimeInSeconds;

    public RouteDto() {
    }

    public RouteDto(RouteEntity entity) {
        copyFromEntity(entity);
    }

    public RouteDto(Long id, double[][] lineString, Long lengthInMeters, Long travelTimeInSeconds) {
        this.id = id;
        this.lineString = lineString;
        this.lengthInMeters = lengthInMeters;
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    @AssertTrue(message = "Line string not given or too long. Max size: " + MAX_LENGTH + " x " + MAX_SUBLENGTH)
    public boolean isLengthValid() {
        if (lineString == null || lineString.length > MAX_LENGTH) {
            return false;
        }
        for (int i = 0; i < lineString.length; i++) {
            if (lineString[i].length > MAX_SUBLENGTH) {
                return false;
            }
        }
        return true;
    }

    @Override
    public RouteEntity toEntity(GeometryFactory geometryFactory) {
        final RouteEntity entity = new RouteEntity();
        applyToEntity(geometryFactory, entity);
        return entity;
    }

    @Override
    public void applyToEntity(GeometryFactory geometryFactory, RouteEntity entity) {
        if (lineString == null) {
            throw new IllegalArgumentException("LineString can't be null");
        }

        entity.setLineString(geometryFactory, lineString);
        entity.setLengthInMeters(lengthInMeters);
        entity.setTravelTimeInSeconds(travelTimeInSeconds);
    }

    @Override
    public void copyFromEntity(RouteEntity entity) {
        id = entity.getId();

        final Coordinate[] coordinates = entity.getLineString().getCoordinates();
        final int pointNumber = coordinates.length;
        lineString = new double[pointNumber][2];
        for (int i = 0; i < pointNumber; i++) {
            final Coordinate coordinate = coordinates[i];
            lineString[i][0] = coordinate.getOrdinate(Coordinate.Y);
            lineString[i][1] = coordinate.getOrdinate(Coordinate.X);
        }

        lengthInMeters = entity.getLengthInMeters();
        travelTimeInSeconds = entity.getTravelTimeInSeconds();
    }

    public Long getId() {
        return id;
    }

    public double[][] getLineString() {
        return lineString;
    }

    public Long getLengthInMeters() {
        return lengthInMeters;
    }

    public Long getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }
}
