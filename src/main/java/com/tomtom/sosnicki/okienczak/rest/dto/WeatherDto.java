package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.WeatherEntity;
import com.vividsolutions.jts.geom.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.AssertTrue;

public final class WeatherDto implements Dto<WeatherEntity> {

    private static final int MAX_LENGTH = 10;
    private static final int MAX_SUBLENGTH = 100;
    private static final int MAX_SUBSUBLENGTH = 100;

    private Long id;
    private double[][][] polygon;

    @Length(max = 255, message = "Comment too long")
    private String comment;

    public WeatherDto() {
    }

    public WeatherDto(WeatherEntity entity) {
        copyFromEntity(entity);
    }

    public WeatherDto(Long id, double[][][] polygon, String comment) {
        this.id = id;
        this.polygon = polygon;
        this.comment = comment;
    }

    @AssertTrue(message = "Polygon not given or too long. Max size: " + MAX_LENGTH + " x " + MAX_SUBLENGTH + " x" + MAX_SUBSUBLENGTH)
    public boolean isLengthValid() {
        if (polygon == null || polygon.length > MAX_LENGTH) {
            return false;
        }
        for (int i = 0; i < polygon.length; i++) {
            if (polygon[i].length > MAX_SUBLENGTH) {
                return false;
            }
            for (int j = 0; j < polygon[i].length; j++) {
                if (polygon[i][j].length > MAX_SUBSUBLENGTH) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public WeatherEntity toEntity(GeometryFactory geometryFactory) {
        final WeatherEntity entity = new WeatherEntity();
        applyToEntity(geometryFactory, entity);
        return entity;
    }

    @Override
    public void applyToEntity(GeometryFactory geometryFactory, WeatherEntity entity) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment can't be null");
        }
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon can't be null");
        }

        entity.setPolygon(geometryFactory, polygon);
        entity.setComment(comment);
    }

    @Override
    public void copyFromEntity(WeatherEntity entity) {
        id = entity.getId();

        final Polygon polygon = entity.getPolygon();
        final int ringNumber = polygon.getNumInteriorRing() + 1;

        this.polygon = new double[ringNumber][][];
        for (int i = 0; i < ringNumber; i++) {
            final LineString ring = i == 0
                    ? polygon.getExteriorRing()
                    : polygon.getInteriorRingN(i - 1);
            final Coordinate[] coordinates = ring.getCoordinates();

            final int pointNumber = ring.getNumPoints();
            this.polygon[i] = new double[pointNumber][2];
            for (int j = 0; j < pointNumber; j++) {
                final Coordinate coordinate = coordinates[j];
                this.polygon[i][j][0] = coordinate.getOrdinate(Coordinate.Y);
                this.polygon[i][j][1] = coordinate.getOrdinate(Coordinate.X);
            }
        }

        comment = entity.getComment();
    }

    public Long getId() {
        return id;
    }

    public double[][][] getPolygon() {
        return polygon;
    }

    public String getComment() {
        return comment;
    }
}
