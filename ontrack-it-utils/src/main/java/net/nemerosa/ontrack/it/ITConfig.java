package net.nemerosa.ontrack.it;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.nemerosa.ontrack.common.RunProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@Profile(RunProfile.UNIT_TEST)
@EnableTransactionManagement
@Import({
        MetricRepositoryAutoConfiguration.class,
        MetricsDropwizardAutoConfiguration.class,
        HealthIndicatorAutoConfiguration.class
})
public class ITConfig {

    private final Logger logger = LoggerFactory.getLogger(ITConfig.class);

    @Bean
    public DataSource dataSource() throws IOException {
        String dbURL = "jdbc:h2:mem:ontrack;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;DEFRAG_ALWAYS=TRUE";
        logger.info("Using database at {}", dbURL);
        org.apache.tomcat.jdbc.pool.DataSource pool = new org.apache.tomcat.jdbc.pool.DataSource();
        pool.setDriverClassName("org.h2.Driver");
        pool.setUrl(dbURL);
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setDefaultAutoCommit(false);
        pool.setInitialSize(1);
        pool.setMaxActive(2);
        return pool;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws IOException {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public ConverterRegistry converterRegistry() {
        return new DefaultConversionService();
    }

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
