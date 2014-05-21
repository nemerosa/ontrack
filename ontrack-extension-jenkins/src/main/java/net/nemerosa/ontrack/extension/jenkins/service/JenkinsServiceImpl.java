package net.nemerosa.ontrack.extension.jenkins.service;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfigurationNotFoundException;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsSettings;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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

    @Override
    public JenkinsConfiguration newConfiguration(JenkinsConfiguration configuration) {
        return configurationRepository.save(configuration);
    }

    @Override
    public JenkinsConfiguration getConfiguration(String name) {
        return configurationRepository
                .find(JenkinsConfiguration.class, name)
                .orElseThrow(() -> new JenkinsConfigurationNotFoundException(name));
    }

    @Override
    public void deleteConfiguration(String name) {
        configurationRepository.delete(JenkinsConfiguration.class, name);
    }

    @Override
    public void updateConfiguration(String name, JenkinsConfiguration configuration) {
        Validate.isTrue(StringUtils.equals(name, configuration.getName()), "Configuration name must match");
        configurationRepository.save(configuration);
    }

}
