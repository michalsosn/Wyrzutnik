package com.tomtom.sosnicki.okienczak.rest.dto;

import com.tomtom.sosnicki.okienczak.entity.DumpsterEntity;
import org.springframework.stereotype.Component;

@Component
public class DumpsterDtoFactory implements DtoFactory<DumpsterEntity, DumpsterDto> {
    @Override
    public DumpsterDto createEmpty() {
        return new DumpsterDto();
    }

    @Override
    public DumpsterDto createFromEntity(DumpsterEntity entity) {
        return new DumpsterDto(entity);
    }
}
