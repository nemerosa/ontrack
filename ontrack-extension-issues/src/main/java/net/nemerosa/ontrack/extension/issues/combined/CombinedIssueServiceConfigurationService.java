package net.nemerosa.ontrack.extension.issues.combined;

import java.util.List;
import java.util.Optional;

public interface CombinedIssueServiceConfigurationService {

    List<CombinedIssueServiceConfiguration> getConfigurationList();

    Optional<CombinedIssueServiceConfiguration> getConfigurationByName(String name);
}
