package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.repository.SpatialRepository;
import com.tomtom.sosnicki.okienczak.rest.dto.Dto;
import com.tomtom.sosnicki.okienczak.rest.dto.DtoFactory;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
public abstract class CrudService<
        ID extends Serializable,
        E,
        D extends Dto<E>,
        R extends CrudRepository<E, ID> & SpatialRepository<E>
> {

    @Autowired
    private R repository;

    @Autowired
    private DtoFactory<E, D> dtoFactory;

    @Autowired
    private GeometryFactory geometryFactory;

    protected abstract Logger getLog();

    protected abstract String getEntityName();

    public List<D> listEntities(double lat0, double lng0, double lat1, double lng1) {
        return repository.findInside(lat0, lng0, lat1, lng1).stream()
                .map(dtoFactory::createFromEntity)
                .collect(Collectors.toList());
    }

    public E addEntity(D dto) {
        E entity = dto.toEntity(geometryFactory);

        getLog().info("Adding {} {}", getEntityName(), entity);

        entity = repository.save(entity);

        return entity;
    }

    public void updateEntity(ID entityId, D dto) {
        final E entity = repository.findOne(entityId);
        if (entity == null) {
            throw new NoSuchElementException(String.format("Entity with id = %s not found", entityId));
        }

        getLog().info("Updating {} {} to {}", getEntityName(), entity, dto);

        dto.applyToEntity(geometryFactory, entity);
    }

    public void deleteEntity(ID entityId) {
        final E entity = repository.findOne(entityId);
        if (entity == null) {
            throw new NoSuchElementException(String.format("Entity with id = %s not found", entityId));
        }

        getLog().info("Deleting {} with id {}", getEntityName(), entityId);

        repository.delete(entity);
    }
}
