package net.nemerosa.ontrack.it;

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
        // Configuration using system properties
        String dbURL = System.getProperty("it.jdbc.url", "jdbc:postgresql://postgresql/ontrack");
        String dbUser = System.getProperty("it.jdbc.user", "ontrack");
        String dbPassword = System.getProperty("it.jdbc.password", "ontrack");

        logger.info("Using database at {}", dbURL);
        org.apache.tomcat.jdbc.pool.DataSource pool = new org.apache.tomcat.jdbc.pool.DataSource();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setUrl(dbURL);
        pool.setUsername(dbUser);
        pool.setPassword(dbPassword);
        pool.setDefaultAutoCommit(false);
        pool.setInitialSize(10);
        pool.setMaxIdle(10);
        pool.setMaxActive(20);
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
}
