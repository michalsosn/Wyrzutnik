package com.tomtom.sosnicki.okienczak.repository;

import com.tomtom.sosnicki.okienczak.entity.RouteEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public interface RouteRepository extends CrudRepository<RouteEntity, Long>, SpatialRepository<RouteEntity> {
    @Override
    @Query(nativeQuery = true, value =
            "SELECT a.* FROM route a " +
            "WHERE INTERSECTS(a.line_string, BuildMBR(:lng0, :lat0, :lng1, :lat1))"
    )
    List<RouteEntity> findInside(@Param("lat0") double lat0, @Param("lng0") double lng0,
                                 @Param("lat1") double lat1, @Param("lng1") double lng1);
}
