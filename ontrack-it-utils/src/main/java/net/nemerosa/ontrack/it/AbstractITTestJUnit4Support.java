package net.nemerosa.ontrack.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.NameDescription;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static net.nemerosa.ontrack.test.TestUtils.uid;

/**
 * @deprecated JUnit is deprecated. Replace with AbstractServiceTestSupport
 */
@RunWith(SpringJUnit4ClassRunner.class)
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
                "spring.rabbitmq.password=ontrack"
        }
)
@Deprecated
public abstract class AbstractITTestJUnit4Support extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    protected DataSource dataSource;

    /**
     * Jdbc template for tests that need a direct access to the database
     */
    protected @NotNull
    JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Named Jdbc template for tests that need a direct access to the database
     */
    protected @NotNull
    NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
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
