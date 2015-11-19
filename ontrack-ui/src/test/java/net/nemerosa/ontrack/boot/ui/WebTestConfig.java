package net.nemerosa.ontrack.boot.ui;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.dropwizard.DropwizardMetricServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true) // Aligned with application.properties
public class WebTestConfig {

    @Autowired
    private MetricRegistry metricRegistry;

    @Bean
    public ConverterRegistry converterRegistry() {
        return new DefaultConversionService();
    }

    @Bean
    public DropwizardMetricServices dropwizardMetricServices() {
        return new DropwizardMetricServices(metricRegistry);
    }

}
