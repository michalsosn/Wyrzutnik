package com.tomtom.sosnicki.okienczak.entity;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PointAttributeConverter implements AttributeConverter<Point, byte[]> {

    private static GeometryFactory geometryFactory;

    public static void setGeometryFactory(GeometryFactory geometryFactory) {
        PointAttributeConverter.geometryFactory = geometryFactory;
    }

    @Override
    public byte[] convertToDatabaseColumn(Point point) {
        return point == null ? null : JtsGeometryMarshaller.toByteArray(point);
    }

    @Override
    public Point convertToEntityAttribute(byte[] data) {
        return data == null ? null : JtsGeometryMarshaller.toPoint(geometryFactory, data);
    }

}
