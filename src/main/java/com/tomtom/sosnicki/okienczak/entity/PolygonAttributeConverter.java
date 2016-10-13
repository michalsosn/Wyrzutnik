package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PolygonAttributeConverter implements AttributeConverter<Polygon, byte[]> {

    private static GeometryFactory geometryFactory;

    public static void setGeometryFactory(GeometryFactory geometryFactory) {
        PolygonAttributeConverter.geometryFactory = geometryFactory;
    }

    @Override
    public byte[] convertToDatabaseColumn(Polygon polygon) {
        return polygon == null ? null : JtsGeometryMarshaller.toByteArray(polygon);
    }

    @Override
    public Polygon convertToEntityAttribute(byte[] data) {
        return data == null ? null : JtsGeometryMarshaller.toPolygon(geometryFactory, data);
    }

}
