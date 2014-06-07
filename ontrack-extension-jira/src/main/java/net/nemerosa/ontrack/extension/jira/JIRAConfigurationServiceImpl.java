package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JIRAConfigurationServiceImpl extends AbstractConfigurationService<JIRAConfiguration> implements JIRAConfigurationService {

    @Autowired
    public JIRAConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        super(JIRAConfiguration.class, configurationRepository, securityService);
    }

}
