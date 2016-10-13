package com.tomtom.sosnicki.okienczak;

import com.tomtom.sosnicki.okienczak.entity.LineStringAttributeConverter;
import com.tomtom.sosnicki.okienczak.entity.PointAttributeConverter;
import com.tomtom.sosnicki.okienczak.entity.PolygonAttributeConverter;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Clock;
import java.util.Properties;

@SpringBootApplication
public class WyrzutnikApplication {

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

	public static void main(String[] args) {
		SpringApplication.run(WyrzutnikApplication.class, args);
	}

    @Bean
    public DataSource dataSource() throws SQLException {
        Properties properties = new Properties();
        properties.put("enable_shared_cache", "true");
        properties.put("enable_load_extension", "true");
        properties.put("enable_spatialite", "true");
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(dataSourceUrl, properties);

        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(10);
        config.setMaxIdle(10);
        ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, config);

        poolableConnectionFactory.setPool(connectionPool);

        return new PoolingDataSource<>(connectionPool);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
    }

    @PostConstruct
    public void initializeConverters() {
        PointAttributeConverter.setGeometryFactory(geometryFactory());
        LineStringAttributeConverter.setGeometryFactory(geometryFactory());
        PolygonAttributeConverter.setGeometryFactory(geometryFactory());
    }

}
