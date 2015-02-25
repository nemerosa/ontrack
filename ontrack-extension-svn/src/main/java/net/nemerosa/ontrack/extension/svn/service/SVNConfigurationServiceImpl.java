package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SVNConfigurationServiceImpl extends AbstractConfigurationService<SVNConfiguration> implements SVNConfigurationService {

    private final SVNRepositoryDao repositoryDao;

    @Autowired
    public SVNConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, SVNRepositoryDao repositoryDao) {
        super(SVNConfiguration.class, configurationRepository, securityService, encryptionService);
        this.repositoryDao = repositoryDao;
    }

    @Override
    public void deleteConfiguration(String name) {
        super.deleteConfiguration(name);
        Integer id = repositoryDao.findByName(name);
        if (id != null) {
            repositoryDao.delete(id);
        }
    }
}
