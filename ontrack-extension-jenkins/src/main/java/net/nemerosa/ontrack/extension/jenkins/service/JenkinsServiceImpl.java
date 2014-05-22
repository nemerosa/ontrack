package net.nemerosa.ontrack.extension.jenkins.service;

import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfiguration;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsConfigurationNotFoundException;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsService;
import net.nemerosa.ontrack.extension.jenkins.model.JenkinsSettings;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class JenkinsServiceImpl implements JenkinsService {

    private final ConfigurationRepository configurationRepository;
    private final SecurityService securityService;

    @Autowired
    public JenkinsServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService) {
        this.configurationRepository = configurationRepository;
        this.securityService = securityService;
    }

    @Override
    public JenkinsSettings getSettings() {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return new JenkinsSettings(
                configurationRepository.list(JenkinsConfiguration.class)
                        .stream()
                        .map(JenkinsConfiguration::obfuscate)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public JenkinsConfiguration newConfiguration(JenkinsConfiguration configuration) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return configurationRepository.save(configuration);
    }

    @Override
    public JenkinsConfiguration getConfiguration(String name) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        return configurationRepository
                .find(JenkinsConfiguration.class, name)
                .orElseThrow(() -> new JenkinsConfigurationNotFoundException(name));
    }

    @Override
    public void deleteConfiguration(String name) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        configurationRepository.delete(JenkinsConfiguration.class, name);
    }

    @Override
    public void updateConfiguration(String name, JenkinsConfiguration configuration) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        Validate.isTrue(StringUtils.equals(name, configuration.getName()), "Configuration name must match");
        configurationRepository.save(configuration);
    }

}
