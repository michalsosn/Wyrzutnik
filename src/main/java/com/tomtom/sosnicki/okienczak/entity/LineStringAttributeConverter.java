package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LineStringAttributeConverter implements AttributeConverter<LineString, byte[]> {

    private static GeometryFactory geometryFactory;

    public static void setGeometryFactory(GeometryFactory geometryFactory) {
        LineStringAttributeConverter.geometryFactory = geometryFactory;
    }

    @Override
    public byte[] convertToDatabaseColumn(LineString lineString) {
        return lineString == null ? null : JtsGeometryMarshaller.toByteArray(lineString);
    }

    @Override
    public LineString convertToEntityAttribute(byte[] data) {
        return data == null ? null : JtsGeometryMarshaller.toLineString(geometryFactory, data);
    }

}
