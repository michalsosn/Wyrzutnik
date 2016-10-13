package com.tomtom.sosnicki.okienczak.service;

import com.tomtom.sosnicki.okienczak.rest.dto.CalculatedRouteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class GoogleService {
    public enum Optimize {
        TRUE(true, 23), FALSE(false, 23);
        private final boolean paramValue;
        private final int waypointLimit;

        Optimize(boolean paramValue, int waypointLimit) {
            this.paramValue = paramValue;
            this.waypointLimit = waypointLimit;
        }

        public boolean getParamValue() {
            return paramValue;
        }

        public int getWaypointLimit() {
            return waypointLimit;
        }
    }

    @Value("${google.key}")
    private String apiKey;

    private final Logger log = LoggerFactory.getLogger(GoogleService.class);

    public CalculatedRouteResponse sendCalculateRouteRequest(
            double[] start, double[] end, double[][] locations, Optimize optimize
    ) {
        final URI uri = UriComponentsBuilder.newInstance()
                .scheme("https").host("maps.googleapis.com")
                .path("/maps/api/directions/json")
                .queryParam("key", apiKey)
                .queryParam("origin", encodeLocation(start))
                .queryParam("destination", encodeLocation(end))
                .queryParam("waypoints", encodeWaypoints(locations, optimize))
                .buildAndExpand()
                .encode()
                .toUri();
        log.info("Sending GET request to {}", uri);
        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, CalculatedRouteResponse.class);
    }

    private String encodeLocation(double[] locations) {
        return String.format("%.5f,%.5f", locations[0], locations[1]);
    }

    private String encodeWaypoints(double[][] locations, Optimize optimize) {
        final StringBuilder waypointsBuilder = new StringBuilder();
        if (optimize.getParamValue()) {
            waypointsBuilder.append("optimize:true|");
        }
        for (int i = 0; i < locations.length; i++) {
            if (i != 0) {
                waypointsBuilder.append('|');
            }
            waypointsBuilder.append(encodeLocation(locations[i]));
        }
        return waypointsBuilder.toString();
    }
}
