package com.tomtom.sosnicki.okienczak.rest.dto;

public interface DtoFactory<E, D extends Dto<E>> {
    D createEmpty();
    D createFromEntity(E entity);
}
