package com.tomtom.sosnicki.okienczak.rest;

import com.tomtom.sosnicki.okienczak.rest.dto.DumpsterDto;
import com.tomtom.sosnicki.okienczak.service.DumpsterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/dumpster")
public class DumpsterController {

    private final DumpsterService dumpsterService;

    @Autowired
    public DumpsterController(DumpsterService dumpsterService) {
        this.dumpsterService = dumpsterService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<DumpsterDto> listDumpsters(
            @RequestParam(name = "lat0") double lat0,
            @RequestParam(name = "lng0") double lng0,
            @RequestParam(name = "lat1") double lat1,
            @RequestParam(name = "lng1") double lng1
    ) {
        return dumpsterService.listEntities(lat0, lng0, lat1, lng1);
    }

    @RequestMapping(method = RequestMethod.POST)
    public long addDumpster(@Valid @RequestBody DumpsterDto dumpsterDto) {
        return dumpsterService.addEntity(dumpsterDto).getId();
    }

    @RequestMapping(path = "/{dumpsterId}", method = RequestMethod.PUT)
    public void updateDumpster(@PathVariable long dumpsterId, @Valid @RequestBody DumpsterDto dumpsterDto) {
        dumpsterService.updateEntity(dumpsterId, dumpsterDto);
    }

    @RequestMapping(path = "/{dumpsterId}", method = RequestMethod.DELETE)
    public void deleteDumpster(@PathVariable long dumpsterId) {
        dumpsterService.deleteEntity(dumpsterId);
    }

}
