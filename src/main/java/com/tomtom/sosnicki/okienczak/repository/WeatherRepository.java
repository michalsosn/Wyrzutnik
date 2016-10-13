package com.tomtom.sosnicki.okienczak.repository;

import com.tomtom.sosnicki.okienczak.entity.WeatherEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public interface WeatherRepository extends CrudRepository<WeatherEntity, Long>, SpatialRepository<WeatherEntity> {
    @Override
    @Query(nativeQuery = true, value =
            "SELECT a.* FROM weather a " +
            "WHERE INTERSECTS(a.polygon, BuildMBR(:lng0, :lat0, :lng1, :lat1))"
    )
    List<WeatherEntity> findInside(@Param("lat0") double lat0, @Param("lng0") double lng0,
                                   @Param("lat1") double lat1, @Param("lng1") double lng1);
}
