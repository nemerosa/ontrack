package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.support.configurations.AbstractConfigurationService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SVNConfigurationServiceImpl extends AbstractConfigurationService<SVNConfiguration> implements SVNConfigurationService {

    @Autowired
    public SVNConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        super(SVNConfiguration.class, configurationRepository, securityService);
    }

}
