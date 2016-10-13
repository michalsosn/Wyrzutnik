package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.WeatherEntity;
import org.springframework.stereotype.Component;

@Component
public class WeatherDtoFactory implements DtoFactory<WeatherEntity, WeatherDto> {
    @Override
    public WeatherDto createEmpty() {
        return new WeatherDto();
    }

    @Override
    public WeatherDto createFromEntity(WeatherEntity entity) {
        return new WeatherDto(entity);
    }
}
