package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.RouteEntity;
import org.springframework.stereotype.Component;

@Component
public class RouteDtoFactory implements DtoFactory<RouteEntity, RouteDto> {
    @Override
    public RouteDto createEmpty() {
        return new RouteDto();
    }

    @Override
    public RouteDto createFromEntity(RouteEntity entity) {
        return new RouteDto(entity);
    }
}
