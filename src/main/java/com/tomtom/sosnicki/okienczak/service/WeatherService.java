package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.entity.WeatherEntity;
import com.tomtom.sosnicki.okienczak.repository.WeatherRepository;
import com.tomtom.sosnicki.okienczak.rest.dto.WeatherDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WeatherService extends CrudService<Long, WeatherEntity, WeatherDto, WeatherRepository> {

    private final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected String getEntityName() {
        return "weather";
    }
}
