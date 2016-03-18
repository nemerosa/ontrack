package net.nemerosa.ontrack.it;

import net.nemerosa.ontrack.common.RunProfile;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile(RunProfile.UNIT_TEST)
@EnableTransactionManagement
@Import({
        MetricRepositoryAutoConfiguration.class,
        MetricsDropwizardAutoConfiguration.class
})
public class ITConfig {

    @Bean
    public ConverterRegistry converterRegistry() {
        return new DefaultConversionService();
    }
}
