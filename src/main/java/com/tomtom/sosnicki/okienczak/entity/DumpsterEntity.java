package com.tomtom.sosnicki.okienczak.entity;

import javax.persistence.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import java.io.Serializable;
import java.time.LocalDate;

@Entity(name = "Dumpster")
@Table(name = "dumpster")
public class DumpsterEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "dumpster_id", nullable = false, updatable = false)
    private long id;

    @Column(name = "point", nullable = false)
    private Point point;

    @Column(name = "intelligent", nullable = false)
    private boolean intelligent;

    @Column(name = "capacity")
    private Double capacity;

    @Column(name = "used")
    private Double used;

    @Column(name = "collection_period_in_days")
    private Integer collectionPeriodInDays;

    @Column(name = "last_collected")
    private LocalDate lastCollected;

    public DumpsterEntity() {
    }

    public DumpsterEntity(
            Point point, Integer collectionPeriodInDays, LocalDate lastCollected
    ) {
        this.point = point;
        this.intelligent = true;
        this.collectionPeriodInDays = collectionPeriodInDays;
        this.lastCollected = lastCollected;
    }

    public DumpsterEntity(
            Point point, Double capacity, Double used) {
        this.point = point;
        this.intelligent = false;
        this.capacity = capacity;
        this.used = used;
    }

    public long getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setPoint(GeometryFactory geometryFactory, double lat, double lng) {
        this.point = geometryFactory.createPoint(new Coordinate(lng, lat));
    }

    public boolean isIntelligent() {
        return intelligent;
    }

    public void setIntelligent(boolean intelligent) {
        this.intelligent = intelligent;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getUsed() {
        return used;
    }

    public void setUsed(Double used) {
        this.used = used;
    }

    public Integer getCollectionPeriodInDays() {
        return collectionPeriodInDays;
    }

    public void setCollectionPeriodInDays(Integer collectionPeriodInDays) {
        this.collectionPeriodInDays = collectionPeriodInDays;
    }

    public LocalDate getLastCollected() {
        return lastCollected;
    }

    public void setLastCollected(LocalDate lastCollected) {
        this.lastCollected = lastCollected;
    }

    @Override
    public String toString() {
        return "DumpsterEntity{" +
                "id=" + id +
                ", point=" + point +
                ", intelligent=" + intelligent +
                ", capacity=" + capacity +
                ", used=" + used +
                ", collectionPeriodInDays=" + collectionPeriodInDays +
                ", lastCollected=" + lastCollected +
                '}';
    }
}
