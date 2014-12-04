package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GitConfigurationServiceImpl extends AbstractConfigurationService<FormerGitConfiguration> implements GitConfigurationService {

    @Autowired
    public GitConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService) {
        super(FormerGitConfiguration.class, configurationRepository, securityService, encryptionService);
    }

}
