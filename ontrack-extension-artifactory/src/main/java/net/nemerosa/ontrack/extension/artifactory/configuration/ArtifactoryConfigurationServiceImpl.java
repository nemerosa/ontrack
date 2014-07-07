package net.nemerosa.ontrack.extension.artifactory.configuration;

import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArtifactoryConfigurationServiceImpl extends AbstractConfigurationService<ArtifactoryConfiguration> implements ArtifactoryConfigurationService {

    @Autowired
    public ArtifactoryConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        super(ArtifactoryConfiguration.class, configurationRepository, securityService);
    }

}
