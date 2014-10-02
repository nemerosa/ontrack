package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestConfigurationServiceImpl extends AbstractConfigurationService<TestConfiguration>
        implements TestConfigurationService {

    @Autowired
    public TestConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService) {
        super(TestConfiguration.class, configurationRepository, securityService, encryptionService);
    }
}
