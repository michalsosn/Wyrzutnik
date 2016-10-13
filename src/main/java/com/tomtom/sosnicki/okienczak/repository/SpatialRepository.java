package com.tomtom.sosnicki.okienczak.repository;

import java.util.List;

public interface SpatialRepository<E> {
    List<E> findInside(double lat0, double lng0, double lat1, double lng1);
}
