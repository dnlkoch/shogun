package de.terrestris.shogun.lib.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.util.ObjectMapperSupplier;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JacksonConfig implements ObjectMapperSupplier {

    private static ObjectMapper mapper;

    @Bean
    public ObjectMapper objectMapper() {
        this.init();
        return mapper;
    }

    @Value("${shogun.srid:4326}")
    protected int srid;

    @Value("${shogun.coordinatePrecisionScale:10}")
    protected int coordinatePrecisionScale;

    @Bean
    public JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(coordinatePrecisionScale), srid);
        return new JtsModule(geomFactory);
    }

    @Override
    public ObjectMapper get() {
        return objectMapper();
    }

    @PostConstruct
    private void init() {
        if (mapper == null) {
            mapper = new ObjectMapper();

            var reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new SubTypesScanner(),
                    new TypeAnnotationsScanner()));


            for (var cl : reflections.getTypesAnnotatedWith(JsonSuperType.class)) {
                var type = cl.getAnnotation(JsonSuperType.class).type();
                mapper.addMixIn(type, cl);
            }
        }
    }
}
