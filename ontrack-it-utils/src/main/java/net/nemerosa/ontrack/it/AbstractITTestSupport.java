package net.nemerosa.ontrack.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.NameDescription;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static net.nemerosa.ontrack.test.TestUtils.uid;

@Transactional
@ActiveProfiles(profiles = {RunProfile.UNIT_TEST})
@SpringBootTest(
        classes = {
                AbstractITTestSupport.AbstractIntegrationTestConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@TestPropertySource(
        properties = {
                "spring.rabbitmq.host=localhost",
                "spring.rabbitmq.username=ontrack",
                "spring.rabbitmq.password=ontrack",
                "spring.graphql.schema.locations=classpath:graphql/",
        }
)
public abstract class AbstractITTestSupport {

    @Autowired
    protected DataSource dataSource;

    /**
     * Named Jdbc template for tests that need a direct access to the database
     */
    protected @NotNull
    NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Configuration
    @Profile(RunProfile.UNIT_TEST)
    @ComponentScan("net.nemerosa.ontrack")
    @EnableAutoConfiguration
    public static class AbstractIntegrationTestConfiguration {

        private final Logger logger = LoggerFactory.getLogger(AbstractITTestSupport.class);

        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }

        @Bean
        public ConverterRegistry converterRegistry() {
            return new DefaultConversionService();
        }

        @Bean
        @Primary
        public DataSourceProperties dataSourceProperties() {
            // Configuration using system properties
            String dbURL = System.getProperty("spring.datasource.url", "jdbc:postgresql://localhost/ontrack");
            String dbUser = System.getProperty("spring.datasource.username", "ontrack");
            String dbPassword = System.getProperty("spring.datasource.password", "ontrack");
            // Logging
            logger.info("[test][jdbc] URL = " + dbURL);
            // Properties
            DataSourceProperties properties = new DataSourceProperties();
            properties.setDriverClassName("org.postgresql.Driver");
            properties.setUrl(dbURL);
            properties.setUsername(dbUser);
            properties.setPassword(dbPassword);
            return properties;
        }
    }

    public static NameDescription nameDescription() {
        String uid = uid("");
        return new NameDescription(
                uid,
                String.format("%s description", uid)
        );
    }

    protected final ObjectMapper objectMapper = ObjectMapperFactory.create();

}
