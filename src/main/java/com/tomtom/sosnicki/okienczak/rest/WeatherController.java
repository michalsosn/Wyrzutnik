package com.tomtom.sosnicki.okienczak.rest;

import com.tomtom.sosnicki.okienczak.rest.dto.WeatherDto;
import com.tomtom.sosnicki.okienczak.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<WeatherDto> listWeathers(
            @RequestParam(name = "lat0") double lat0,
            @RequestParam(name = "lng0") double lng0,
            @RequestParam(name = "lat1") double lat1,
            @RequestParam(name = "lng1") double lng1
    ) {
        return weatherService.listEntities(lat0, lng0, lat1, lng1);
    }

    @RequestMapping(method = RequestMethod.POST)
    public long addWeather(@Valid @RequestBody WeatherDto weatherDto) {
        return weatherService.addEntity(weatherDto).getId();
    }

    @RequestMapping(path = "/{weatherId}", method = RequestMethod.PUT)
    public void updateWeather(@PathVariable long weatherId, @Valid @RequestBody WeatherDto weatherDto) {
        weatherService.updateEntity(weatherId, weatherDto);
    }

    @RequestMapping(path = "/{weatherId}", method = RequestMethod.DELETE)
    public void deleteWeather(@PathVariable long weatherId) {
        weatherService.deleteEntity(weatherId);
    }
}
