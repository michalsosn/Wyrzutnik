package com.tomtom.sosnicki.okienczak.repository;

import com.tomtom.sosnicki.okienczak.entity.DumpsterEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(propagation = Propagation.MANDATORY)
public interface DumpsterRepository extends CrudRepository<DumpsterEntity, Long>, SpatialRepository<DumpsterEntity> {

    @Override
    @Query(nativeQuery = true, value =
            "SELECT a.* FROM dumpster a " +
            "WHERE INTERSECTS(a.point, BuildMBR(:lng0, :lat0, :lng1, :lat1))"
    )
    List<DumpsterEntity> findInside(@Param("lat0") double lat0, @Param("lng0") double lng0,
                                    @Param("lat1") double lat1, @Param("lng1") double lng1);

    @Query(nativeQuery = true, value =
            "SELECT a.* FROM dumpster a, dumpster b " +
            "WHERE b.dumpster_id = :referenceId " +
            "AND DISTANCE(a.point, b.point) < :maxDistance"
    )
    List<DumpsterEntity> findNear(@Param("referenceId") long referenceId, @Param("maxDistance") double maxDistance);

    @Query(nativeQuery = true, value =
            "SELECT * FROM dumpster " +
            "WHERE " +
            "(NOT intelligent AND (" +
                    "last_collected IS NULL " +
                    "OR date(last_collected / 1000, 'unixepoch', 'localtime') <= date('now', '-' || collection_period_in_days || ' day')" +
            ")) OR (intelligent AND " +
                    "used / capacity >= :usedThreshold" +
            ") ORDER BY DISTANCE(dumpster.point, GeomFromText('POINT(' || :lat || ', ' || :lng || ')')) " +
            "LIMIT :limit"
    )
    List<DumpsterEntity> findFullOrderedByDistance(@Param("lat") double lat, @Param("lng") double lng,
                                                   @Param("usedThreshold") double usedThreshold,
                                                   @Param("limit") int limit);
}
