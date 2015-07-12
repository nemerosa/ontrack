package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

import java.util.List;
import java.util.Optional;

public interface CombinedIssueServiceConfigurationService {

    List<CombinedIssueServiceConfiguration> getConfigurationList();

    Optional<CombinedIssueServiceConfiguration> getConfigurationByName(String name);

    List<IssueServiceConfigurationRepresentation> getAvailableIssueServiceConfigurations();

    CombinedIssueServiceConfiguration newConfiguration(CombinedIssueServiceConfiguration configuration);

    CombinedIssueServiceConfiguration getConfiguration(String name);

    void deleteConfiguration(String name);

    void updateConfiguration(String name, CombinedIssueServiceConfiguration configuration);
}
