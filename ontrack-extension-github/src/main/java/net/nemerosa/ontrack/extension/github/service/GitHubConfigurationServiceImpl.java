package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GitHubConfigurationServiceImpl extends AbstractConfigurationService<GitHubConfiguration> implements GitHubConfigurationService {

    @Autowired
    public GitHubConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService) {
        super(GitHubConfiguration.class, configurationRepository, securityService, encryptionService);
    }

}
