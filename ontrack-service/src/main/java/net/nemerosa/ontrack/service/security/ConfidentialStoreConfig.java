package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.support.EnvService;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Configuration of the key store.
 */
@Configuration
public class ConfidentialStoreConfig {

    private final OntrackConfigProperties configProperties;
    private final EnvService envService;

    @Autowired
    public ConfidentialStoreConfig(OntrackConfigProperties configProperties, EnvService envService) {
        this.configProperties = configProperties;
        this.envService = envService;
    }

    @Bean
    public ConfidentialStore getConfidentialStore() throws IOException, InterruptedException {
        return new FileConfidentialStore(envService);
    }
}
