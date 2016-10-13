package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "route")
@Table(name = "route")
public class RouteEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "route_id", nullable = false, updatable = false)
    private long id;

    @Column(name = "line_string", nullable = false)
    private LineString lineString;

    @Column(name = "length_in_meters")
    private Long lengthInMeters;

    @Column(name = "travel_time_in_seconds")
    private Long travelTimeInSeconds;

    public RouteEntity() {
    }

    public RouteEntity(LineString lineString, Long travelTimeInSeconds, Long lengthInMeters) {
        this.lineString = lineString;
        this.travelTimeInSeconds = travelTimeInSeconds;
        this.lengthInMeters = lengthInMeters;
    }

    public long getId() {
        return id;
    }

    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
    }

    public void setLineString(GeometryFactory geometryFactory, double[][] lineString) {
        final int pointNumber = lineString.length;
        final Coordinate[] coordinates = new Coordinate[pointNumber];
        for (int i = 0; i < pointNumber; i++) {
            coordinates[i] = new Coordinate(lineString[i][1], lineString[i][0]);
        }

        this.lineString = geometryFactory.createLineString(coordinates);
    }

    public Long getLengthInMeters() {
        return lengthInMeters;
    }

    public void setLengthInMeters(Long lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    public Long getTravelTimeInSeconds() {
        return travelTimeInSeconds;
    }

    public void setTravelTimeInSeconds(Long travelTimeInSeconds) {
        this.travelTimeInSeconds = travelTimeInSeconds;
    }

    @Override
    public String toString() {
        return "RouteEntity{" +
                "id=" + id +
                ", lineString=" + lineString +
                ", travelTimeInSeconds=" + travelTimeInSeconds +
                ", lengthInMeters=" + lengthInMeters +
                '}';
    }
}
