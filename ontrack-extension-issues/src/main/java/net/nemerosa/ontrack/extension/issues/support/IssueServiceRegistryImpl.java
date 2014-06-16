package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.api.AvailableExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IssueServiceRegistryImpl implements IssueServiceRegistry {

    private final ExtensionManager extensionManager;
    private final Map<String, IssueServiceExtension> extensions;

    @Autowired
    public IssueServiceRegistryImpl(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
        // Indexation of all issue services
        this.extensions = extensionManager.getAllExtensions(IssueServiceExtension.class).stream()
                .map(AvailableExtension::getExtension)
                .collect(Collectors.toMap(
                                IssueServiceExtension::getId,
                                x -> x
                        )
                );
    }

    @Override
    public IssueServiceExtension getIssueService(String id) {
        return getOptionalIssueService(id)
                .filter(extensionManager::isExtensionEnabled)
                .orElseThrow(() -> new IssueServiceNotAvailableException(id));
    }

    @Override
    public Optional<IssueServiceExtension> getOptionalIssueService(String id) {
        return Optional.ofNullable(extensions.get(id));
    }

    @Override
    public List<IssueServiceConfigurationRepresentation> getAvailableIssueServiceConfigurations() {
        List<IssueServiceConfigurationRepresentation> issueServiceConfigurationRepresentations = new ArrayList<>();
        for (IssueServiceExtension issueServiceExtension : extensions.values()) {
            List<? extends IssueServiceConfiguration> configurationList = issueServiceExtension.getConfigurationList();
            for (IssueServiceConfiguration issueServiceConfiguration : configurationList) {
                issueServiceConfigurationRepresentations.add(
                        IssueServiceConfigurationRepresentation.of(
                                issueServiceExtension,
                                issueServiceConfiguration
                        )
                );
            }
        }
        return issueServiceConfigurationRepresentations;
    }

    @Override
    public IssueServiceConfiguration getIssueServiceConfigurationById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        } else {
            return getIssueServiceConfigurationById(IssueServiceConfigurationIdentifier.parse(id));
        }
    }

    private IssueServiceConfiguration getIssueServiceConfigurationById(IssueServiceConfigurationIdentifier identifier) {
        Optional<IssueServiceExtension> issueService = getOptionalIssueService(identifier.getServiceId());
        if (issueService.isPresent()) {
            return issueService.get().getConfigurationByName(identifier.getName());
        } else {
            return null;
        }
    }
}
