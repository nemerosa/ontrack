package net.nemerosa.ontrack.it;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.nemerosa.ontrack.common.RunProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class ITConfig {

    private final Logger logger = LoggerFactory.getLogger(ITConfig.class);

    @Bean
    public DataSource dataSource() {
        // Configuration using system properties
        String dbURL = System.getProperty("it.jdbc.url", "jdbc:postgresql://localhost/ontrack");
        String dbUser = System.getProperty("it.jdbc.user", "ontrack");
        String dbPassword = System.getProperty("it.jdbc.password", "ontrack");

        logger.info("Using database at {}", dbURL);
        HikariDataSource pool = new HikariDataSource();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setJdbcUrl(dbURL);
        pool.setUsername(dbUser);
        pool.setPassword(dbPassword);
        pool.setAutoCommit(false);
        pool.setMaximumPoolSize(20);
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
