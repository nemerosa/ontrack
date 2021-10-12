package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.support.ConfigurationNotFoundException;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CombinedIssueServiceConfigurationServiceImpl implements CombinedIssueServiceConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final IssueServiceRegistry issueServiceRegistry;
    private final SecurityService securityService;

    @Autowired
    public CombinedIssueServiceConfigurationServiceImpl(ConfigurationRepository configurationRepository, IssueServiceRegistry issueServiceRegistry, SecurityService securityService) {
        this.configurationRepository = configurationRepository;
        this.issueServiceRegistry = issueServiceRegistry;
        this.securityService = securityService;
    }

    @Override
    public List<CombinedIssueServiceConfiguration> getConfigurationList() {
        return configurationRepository.list(CombinedIssueServiceConfiguration.class);
    }

    @Override
    public Optional<CombinedIssueServiceConfiguration> getConfigurationByName(String name) {
        return Optional.ofNullable(configurationRepository.find(CombinedIssueServiceConfiguration.class, name));
    }

    @Override
    public List<IssueServiceConfigurationRepresentation> getAvailableIssueServiceConfigurations() {
        return issueServiceRegistry.getAvailableIssueServiceConfigurations().stream()
                // Gets only the issue services which are NOT combined
                .filter(
                        issueServiceConfigurationRepresentation ->
                                !StringUtils.equals(
                                        issueServiceConfigurationRepresentation.getServiceId(),
                                        CombinedIssueServiceExtension.SERVICE
                                )
                )
                .collect(Collectors.toList());
    }

    @Override
    public CombinedIssueServiceConfiguration newConfiguration(CombinedIssueServiceConfiguration configuration) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        configurationRepository.save(configuration);
        return configuration;
    }

    @Override
    public CombinedIssueServiceConfiguration getConfiguration(String name) {
        return getConfigurationByName(name)
                .orElseThrow(() -> new ConfigurationNotFoundException(name));
    }

    @Override
    public void deleteConfiguration(String name) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        configurationRepository.delete(CombinedIssueServiceConfiguration.class, name);
    }

    @Override
    public void updateConfiguration(String name, CombinedIssueServiceConfiguration configuration) {
        securityService.checkGlobalFunction(GlobalSettings.class);
        Validate.isTrue(StringUtils.equals(name, configuration.getName()), "Configuration name must match");
        configurationRepository.save(configuration);
    }
}
