package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.DumpsterEntity;
import com.vividsolutions.jts.geom.GeometryFactory;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

public final class DumpsterDto implements Dto<DumpsterEntity> {
    private Long id;
    private double lat;
    private double lng;
    private boolean intelligent;
    private Double capacity;
    private Double used;
    private Integer collectionPeriodInDays;
    private LocalDate lastCollected;

    public DumpsterDto() {
    }

    public DumpsterDto(
            Long id, double lat, double lng, boolean intelligent, Double capacity, Double used,
            Integer collectionPeriodInDays, LocalDate lastCollected
    ) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.intelligent = intelligent;
        this.capacity = capacity;
        this.used = used;
        this.collectionPeriodInDays = collectionPeriodInDays;
        this.lastCollected = lastCollected;
    }

    public DumpsterDto(DumpsterEntity entity) {
        copyFromEntity(entity);
    }

    @AssertTrue(message = "Intelligent dumpster without capacity or used set")
    public boolean isIntelligentValid() {
        if (intelligent && (capacity == null || used == null)) {
            return false;
        }
        if (!intelligent && collectionPeriodInDays == null) {
            return false;
        }
        return true;
    }

    @AssertTrue(message = "Dumb dumpster without collection period set")
    public boolean isDumbValid() {
        if (!intelligent && collectionPeriodInDays == null) {
            return false;
        }
        return true;
    }

    @Override
    public DumpsterEntity toEntity(GeometryFactory geometryFactory) {
        final DumpsterEntity entity = new DumpsterEntity();
        applyToEntity(geometryFactory, entity);
        return entity;
    }

    @Override
    public void applyToEntity(GeometryFactory geometryFactory, DumpsterEntity entity) {
        entity.setPoint(geometryFactory, lat, lng);
        entity.setIntelligent(intelligent);
        entity.setCapacity(capacity);
        entity.setUsed(used);
        entity.setCollectionPeriodInDays(collectionPeriodInDays);
        entity.setLastCollected(lastCollected);
    }

    @Override
    public void copyFromEntity(DumpsterEntity entity) {
        id = entity.getId();
        lng = entity.getPoint().getX();
        lat = entity.getPoint().getY();
        intelligent = entity.isIntelligent();
        capacity = entity.getCapacity();
        used = entity.getUsed();
        collectionPeriodInDays = entity.getCollectionPeriodInDays();
        lastCollected = entity.getLastCollected();
    }

    public Long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isIntelligent() {
        return intelligent;
    }

    public Double getCapacity() {
        return capacity;
    }

    public Double getUsed() {
        return used;
    }

    public Integer getCollectionPeriodInDays() {
        return collectionPeriodInDays;
    }

    public LocalDate getLastCollected() {
        return lastCollected;
    }

    @Override
    public String toString() {
        return "DumpsterDto{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                ", intelligent=" + intelligent +
                ", capacity=" + capacity +
                ", used=" + used +
                ", collectionPeriodInDays=" + collectionPeriodInDays +
                ", lastCollected=" + lastCollected +
                '}';
    }
}
