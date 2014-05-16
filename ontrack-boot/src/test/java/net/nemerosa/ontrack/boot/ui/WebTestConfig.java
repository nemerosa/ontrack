package net.nemerosa.ontrack.boot.ui;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@EnableAspectJAutoProxy
public class WebTestConfig {

    @Bean
    public ConverterRegistry converterRegistry() {
        return new DefaultConversionService();
    }

}
