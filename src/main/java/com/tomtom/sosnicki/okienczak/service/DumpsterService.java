package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.entity.DumpsterEntity;
import com.tomtom.sosnicki.okienczak.repository.DumpsterRepository;
import com.tomtom.sosnicki.okienczak.rest.dto.DumpsterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DumpsterService extends CrudService<Long, DumpsterEntity, DumpsterDto, DumpsterRepository> {

    private final Logger log = LoggerFactory.getLogger(DumpsterService.class);

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected String getEntityName() {
        return "dumpster";
    }

}
