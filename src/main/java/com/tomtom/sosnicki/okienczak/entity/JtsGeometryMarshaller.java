package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Implements a small part of the specification at https://www.gaia-gis.it/gaia-sins/BLOB-Geometry.html
 */
public class JtsGeometryMarshaller {

    @FunctionalInterface
    private interface TypeMarshaller {
        void write(DataOutputStream dataOutputStream) throws IOException;
    }

    private interface TypeUnmarshaller<T> {
        T read(ByteBuffer byteBuffer);
    }

    private JtsGeometryMarshaller() {
    }

    public static byte[] toByteArray(Point point) {
        return toByteArray(point, 1, dataStream -> {
            dataStream.writeDouble(point.getX());
            dataStream.writeDouble(point.getY());
        });
    }

    public static byte[] toByteArray(LineString lineString) {
        return toByteArray(lineString, 2, dataStream -> {
            dataStream.writeInt(lineString.getNumPoints());

            for (Coordinate coordinate : lineString.getCoordinates()) {
                dataStream.writeDouble(coordinate.getOrdinate(Coordinate.X));
                dataStream.writeDouble(coordinate.getOrdinate(Coordinate.Y));
            }
        });
    }

    public static byte[] toByteArray(Polygon polygon) {
        return toByteArray(polygon, 3, dataStream -> {
            final int ringNumber = polygon.getNumInteriorRing() + 1;
            dataStream.writeInt(ringNumber);

            for (int i = 0; i < ringNumber; i++) {
                final LineString ring = i == 0
                        ? polygon.getExteriorRing()
                        : polygon.getInteriorRingN(i - 1);

                dataStream.writeInt(ring.getNumPoints());
                for (Coordinate coordinate : ring.getCoordinates()) {
                    dataStream.writeDouble(coordinate.getOrdinate(Coordinate.X));
                    dataStream.writeDouble(coordinate.getOrdinate(Coordinate.Y));
                }
            }
        });
    }

    private static <T extends Geometry> byte[] toByteArray(T geometry, int classType, TypeMarshaller typeMarshaller) {
        try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
             DataOutputStream dataStream = new DataOutputStream(byteArrayStream)) {
            dataStream.writeByte(0); // must start with 0
            dataStream.writeByte(0); // 0 - big endian

            dataStream.writeInt(geometry.getSRID());

            final Envelope envelope = geometry.getEnvelopeInternal();
            dataStream.writeDouble(envelope.getMinX());
            dataStream.writeDouble(envelope.getMinY());
            dataStream.writeDouble(envelope.getMaxX());
            dataStream.writeDouble(envelope.getMaxY());

            dataStream.writeByte(0x7C); // a GEOMETRY encoded BLOB value must always have an 0x7C byte in this position

            dataStream.writeInt(classType); // a 32-bits integer; must identify a valid WKB class

            typeMarshaller.write(dataStream);

            dataStream.writeByte(0xFE); // a GEOMETRY encoded BLOB value must always end with a 0xFE byte
            return byteArrayStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static Point toPoint(GeometryFactory geometryFactory, byte[] data) {
        return toGeometry(data, 1, buffer -> {
            final double x = buffer.getDouble();
            final double y = buffer.getDouble();

            return geometryFactory.createPoint(new Coordinate(x, y));
        });
    }

    public static LineString toLineString(GeometryFactory geometryFactory, byte[] data) {
        return toGeometry(data, 2, buffer -> {
            final int pointNumber = buffer.getInt();

            final Coordinate[] coordinates = new Coordinate[pointNumber];
            for (int i = 0; i < pointNumber; i++) {
                final double x = buffer.getDouble();
                final double y = buffer.getDouble();
                coordinates[i] = new Coordinate(x, y);
            }

            return geometryFactory.createLineString(coordinates);
        });
    }

    public static Polygon toPolygon(GeometryFactory geometryFactory, byte[] data) {
        return toGeometry(data, 3, buffer -> {

            final int ringNumber = buffer.getInt();
            LinearRing shell = null;
            LinearRing[] holes = new LinearRing[ringNumber - 1];

            for (int i = 0; i < ringNumber; i++) {
                final int pointNumber = buffer.getInt();
                final Coordinate[] coordinates = new Coordinate[pointNumber];
                for (int j = 0; j < pointNumber; j++) {
                    final double x = buffer.getDouble();
                    final double y = buffer.getDouble();
                    coordinates[j] = new Coordinate(x, y);
                }
                final LinearRing ring = geometryFactory.createLinearRing(coordinates);
                if (i == 0) {
                    shell = ring;
                } else {
                    holes[i - 1] = ring;
                }
            }

            return geometryFactory.createPolygon(shell, holes);
        });
    }

    private static <T> T toGeometry(byte[] data, int classId, TypeUnmarshaller<T> typeUnmarshaller) {
        final ByteBuffer buffer = ByteBuffer.wrap(data);

        check((byte) 0, buffer.get());
        final boolean swapEndianness = buffer.get() > 0;
        if (swapEndianness) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        final int srid = buffer.getInt();

        final double minX = buffer.getDouble();
        final double minY = buffer.getDouble();
        final double maxX = buffer.getDouble();
        final double maxY = buffer.getDouble();

        check((byte) 0x7C, buffer.get());

        check(classId, buffer.getInt());

        final T result = typeUnmarshaller.read(buffer);

        check((byte) 0xFE, buffer.get());

        return result;
    }

    private static <T> void check(T expected, T actual) {
        if (!expected.equals(actual)) {
            throw new IllegalArgumentException(String.format(
                    "Actual value %s is not equal to %s", actual, expected
            ));
        }
    }
}
