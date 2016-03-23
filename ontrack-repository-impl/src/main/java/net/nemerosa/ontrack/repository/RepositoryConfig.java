package net.nemerosa.ontrack.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Configuration
public class RepositoryConfig {

    @Autowired
    private FlywayProperties flywayProperties;

    @PostConstruct
    public void start() {
        flywayProperties.setLocations(Collections.singletonList("classpath:/ontrack/sql"));
    }

}
