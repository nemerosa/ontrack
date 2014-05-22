package net.nemerosa.ontrack.extension.jenkins;

import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JenkinsConfigurationServiceImpl extends AbstractConfigurationService<JenkinsConfiguration> implements JenkinsConfigurationService {

    @Autowired
    public JenkinsConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        super(JenkinsConfiguration.class, configurationRepository, securityService);
    }

}
