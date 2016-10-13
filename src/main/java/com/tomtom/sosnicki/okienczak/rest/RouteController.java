package com.tomtom.sosnicki.okienczak.rest;

import com.tomtom.sosnicki.okienczak.rest.dto.OptimalRouteRequest;
import com.tomtom.sosnicki.okienczak.rest.dto.RouteDto;
import com.tomtom.sosnicki.okienczak.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rest/route")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<RouteDto> listRoutes(
            @RequestParam(name = "lat0") double lat0,
            @RequestParam(name = "lng0") double lng0,
            @RequestParam(name = "lat1") double lat1,
            @RequestParam(name = "lng1") double lng1
    ) {
        return routeService.listEntities(lat0, lng0, lat1, lng1);
    }

    @RequestMapping(method = RequestMethod.POST)
    public long addRoute(@Valid @RequestBody RouteDto routeDto) {
        return routeService.addEntity(routeDto).getId();
    }

    @RequestMapping(path = "/optimal", method = RequestMethod.POST)
    public long createOptimalRoute(@Valid @RequestBody OptimalRouteRequest request) {
        return routeService.createOptimalRouteToFullDumpsters(
                request.getLatitude(), request.getLongitude(),
                request.getUsedThreshold() == null ? 0.8 : request.getUsedThreshold()
        ).getId();
    }

    @RequestMapping(path = "/{routeId}", method = RequestMethod.PUT)
    public void updateRoute(@PathVariable long routeId, @Valid @RequestBody RouteDto routeDto) {
        routeService.updateEntity(routeId, routeDto);
    }

    @RequestMapping(path = "/{routeId}", method = RequestMethod.DELETE)
    public void deleteRoute(@PathVariable long routeId) {
        routeService.deleteEntity(routeId);
    }

}
