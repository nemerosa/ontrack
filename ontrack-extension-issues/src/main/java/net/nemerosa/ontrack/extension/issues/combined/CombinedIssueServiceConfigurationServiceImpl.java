package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CombinedIssueServiceConfigurationServiceImpl implements CombinedIssueServiceConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final IssueServiceRegistry issueServiceRegistry;

    @Autowired
    public CombinedIssueServiceConfigurationServiceImpl(ConfigurationRepository configurationRepository, IssueServiceRegistry issueServiceRegistry) {
        this.configurationRepository = configurationRepository;
        this.issueServiceRegistry = issueServiceRegistry;
    }

    @Override
    public List<CombinedIssueServiceConfiguration> getConfigurationList() {
        return configurationRepository.list(CombinedIssueServiceConfiguration.class);
    }

    @Override
    public Optional<CombinedIssueServiceConfiguration> getConfigurationByName(String name) {
        return configurationRepository.find(CombinedIssueServiceConfiguration.class, name);
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
}
