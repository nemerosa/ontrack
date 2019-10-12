package net.nemerosa.ontrack.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.NameDescription;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static net.nemerosa.ontrack.test.TestUtils.uid;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ActiveProfiles(profiles = {RunProfile.UNIT_TEST})
@SpringBootTest(
        classes = {
                AbstractITTestSupport.AbstractIntegrationTestConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
public abstract class AbstractITTestSupport extends AbstractTransactionalJUnit4SpringContextTests {

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
