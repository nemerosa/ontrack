package net.nemerosa.ontrack.it;

import net.nemerosa.ontrack.common.RunProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
    public DataSource dataSource() throws IOException {
        String dbURL = "jdbc:h2:mem:iteach";
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
    public PlatformTransactionManager txManager() throws IOException {
        return new DataSourceTransactionManager(dataSource());
    }
}
