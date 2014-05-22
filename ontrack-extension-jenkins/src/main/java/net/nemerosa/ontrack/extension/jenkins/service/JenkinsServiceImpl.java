package net.nemerosa.ontrack.extension.jenkins.service;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JenkinsServiceImpl extends AbstractConfigurationService<JenkinsConfiguration> implements JenkinsService {

    @Autowired
    public JenkinsServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        super(JenkinsConfiguration.class, configurationRepository, securityService);
    }

}
