package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "weather")
@Table(name = "weather")
public class WeatherEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "weather_id", nullable = false, updatable = false)
    private long id;

    @Column(name = "polygon", nullable = false)
    private Polygon polygon;

    @Column(name = "comment", nullable = false)
    private String comment;

    public WeatherEntity() {
    }

    public WeatherEntity(Polygon polygon, String comment) {
        this.polygon = polygon;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public void setPolygon(GeometryFactory geometryFactory, double[][][] polygon) {

        final int ringNumber = polygon.length;

        LinearRing shell = null;
        LinearRing[] holes = new LinearRing[ringNumber - 1];
        for (int i = 0; i < ringNumber; i++) {
            final int pointNumber = polygon[i].length;

            final Coordinate[] coordinates = new Coordinate[pointNumber];
            for (int j = 0; j < pointNumber; j++) {
                coordinates[j] = new Coordinate(polygon[i][j][1], polygon[i][j][0]);
            }

            final LinearRing ring = geometryFactory.createLinearRing(coordinates);
            if (i == 0) {
                shell = ring;
            } else {
                holes[i - 1] = ring;
            }
        }

        this.polygon = geometryFactory.createPolygon(shell, holes);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "WeatherEntity{" +
                "id=" + id +
                ", polygon=" + polygon +
                ", comment='" + comment + '\'' +
                '}';
    }
}
