package de.terrestris.shogun.lib.config;


import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        return mapper;
    }

    @Bean
    public JtsModule jtsModule() {
        GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(10), 4326);

        return new JtsModule(geomFactory);
    }

}
