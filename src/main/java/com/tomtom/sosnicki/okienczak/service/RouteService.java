package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.entity.DumpsterEntity;
import com.tomtom.sosnicki.okienczak.entity.RouteEntity;
import com.tomtom.sosnicki.okienczak.repository.DumpsterRepository;
import com.tomtom.sosnicki.okienczak.repository.RouteRepository;
import com.tomtom.sosnicki.okienczak.rest.dto.CalculatedRouteResponse;
import com.tomtom.sosnicki.okienczak.rest.dto.RouteDto;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tomtom.sosnicki.okienczak.service.GoogleService.*;

@Service
public class RouteService extends CrudService<Long, RouteEntity, RouteDto, RouteRepository> {

    private final Logger log = LoggerFactory.getLogger(RouteService.class);

    @Autowired
    private GoogleService googleService;
    @Autowired
    private DumpsterRepository dumpsterRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private GeometryFactory geometryFactory;

    public RouteEntity createOptimalRouteToFullDumpsters(double lat, double lng, double usedThreshold) {
        final List<DumpsterEntity> fullDumpsters = dumpsterRepository.findFullOrderedByDistance(
                lat, lng, usedThreshold, Optimize.TRUE.getWaypointLimit()
        );

        final double[][] lineString = fullDumpsters.stream()
                .map(entity -> toArray(entity.getPoint()))
                .toArray(double[][]::new);
        final double[] startAndEnd = new double[] { lat, lng };

        final CalculatedRouteResponse response = googleService.sendCalculateRouteRequest(
                startAndEnd, startAndEnd, lineString, Optimize.TRUE
        );

        final RouteEntity routeEntity = responseToEntity(geometryFactory, response);
        return routeRepository.save(routeEntity);
    }

    private double[] toArray(Point point) {
        return new double[] { point.getY(), point.getX() };
    }

    private RouteEntity responseToEntity(GeometryFactory geometryFactory, CalculatedRouteResponse response) {
        final RouteEntity routeEntity = new RouteEntity();

        if (response.getRoutes().size() != 1) {
            throw new IllegalArgumentException(String.format(
                    "Request resulted in %d calculated routes, while 1 is expected.", response.getRoutes().size()
            ));
        }
        final CalculatedRouteResponse.Route route = response.getRoutes().get(0);
        route.getLegs().remove(route.getLegs().size() - 1);
        final double[][] calculatedLineString = route.concatLegs();
        routeEntity.setLineString(geometryFactory, calculatedLineString);

        routeEntity.setLengthInMeters(route.sumDistance());
        routeEntity.setTravelTimeInSeconds(route.sumDuration());

        return routeEntity;
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected String getEntityName() {
        return "route";
    }
}
