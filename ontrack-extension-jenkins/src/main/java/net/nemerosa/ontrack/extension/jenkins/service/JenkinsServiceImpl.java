package net.nemerosa.ontrack.extension.jenkins.service;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsSettings;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JenkinsServiceImpl implements JenkinsService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public JenkinsServiceImpl(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public JenkinsSettings getSettings() {
        return new JenkinsSettings(
                configurationRepository.list(JenkinsConfiguration.class)
        );
    }

}
