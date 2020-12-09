package de.terrestris.shoguncore.config;

import de.terrestris.shoguncore.service.ApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class TestContext {

    @Bean
    public ApplicationService applicationServiceMock() {
        return mock(ApplicationService.class);
    }
}
