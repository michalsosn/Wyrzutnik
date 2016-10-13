package com.tomtom.sosnicki.okienczak.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tomtom.sosnicki.okienczak.service.GoogleUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CalculatedRouteResponse {

    public static class Route {
        public static class Leg {
            public static class Step {
                public static class Polyline {
                    private String points;

                    public String getPoints() {
                        return points;
                    }

                    public double[][] decodePoints() {
                        return GoogleUtils.decodePolyline(points);
                    }
                }

                private Polyline polyline;

                public Polyline getPolyline() {
                    return polyline;
                }

                public Stream<double[]> toStream() {
                    return Arrays.stream(polyline.decodePoints());
                }
            }

            public static class ValuePack {
                private long value;
                private String text;

                public long getValue() {
                    return value;
                }

                public String getText() {
                    return text;
                }
            }

            private List<Step> steps;

            private ValuePack distance;

            private ValuePack duration;

            public List<Step> getSteps() {
                return steps;
            }

            public ValuePack getDistance() {
                return distance;
            }

            public ValuePack getDuration() {
                return duration;
            }

            public Stream<double[]> concatSteps() {
                return steps.stream().flatMap(Step::toStream);
            }
        }

        private List<Leg> legs;

        public List<Leg> getLegs() {
            return legs;
        }

        public double[][] concatLegs() {
            return concatLegs(0, legs.size());
        }

        public double[][] concatLegs(int skip, int limit) {
            return legs.stream().skip(skip).limit(limit)
                    .flatMap(Leg::concatSteps)
                    .toArray(double[][]::new);
        }

        public long sumDistance() {
            return legs.stream().map(Leg::getDistance).mapToLong(Leg.ValuePack::getValue).sum();
        }

        public long sumDuration() {
            return legs.stream().map(Leg::getDuration).mapToLong(Leg.ValuePack::getValue).sum();
        }
    }

    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

}
