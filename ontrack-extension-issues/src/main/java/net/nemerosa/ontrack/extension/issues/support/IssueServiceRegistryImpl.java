package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationIdentifier;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueServiceRegistryImpl implements IssueServiceRegistry {

    private final ExtensionManager extensionManager;

    @Autowired
    public IssueServiceRegistryImpl(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    protected Map<String, IssueServiceExtension> getIssueServiceExtensionMap() {
        return extensionManager.getExtensions(IssueServiceExtension.class).stream()
                .collect(Collectors.toMap(
                                IssueServiceExtension::getId,
                                x -> x
                        )
                );
    }

    @Override
    public Collection<IssueServiceExtension> getIssueServices() {
        return getIssueServiceExtensionMap().values();
    }

    @Override
    public IssueServiceExtension getIssueService(String id) {
        return getOptionalIssueService(id)
                .orElseThrow(() -> new IssueServiceNotAvailableException(id));
    }

    @Override
    public Optional<IssueServiceExtension> getOptionalIssueService(String id) {
        return Optional.ofNullable(getIssueServiceExtensionMap().get(id));
    }

    @Override
    public List<IssueServiceConfigurationRepresentation> getAvailableIssueServiceConfigurations() {
        List<IssueServiceConfigurationRepresentation> issueServiceConfigurationRepresentations = new ArrayList<>();
        for (IssueServiceExtension issueServiceExtension : getIssueServiceExtensionMap().values()) {
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

    @Override
    public ConfiguredIssueService getConfiguredIssueService(String issueServiceConfigurationIdentifier) {
        // Parsing
        IssueServiceConfigurationIdentifier identifier = IssueServiceConfigurationIdentifier.parse(issueServiceConfigurationIdentifier);
        if (identifier != null) {
            Optional<IssueServiceExtension> issueService = getOptionalIssueService(identifier.getServiceId());
            return issueService.map(issueServiceExtension -> new ConfiguredIssueService(
                    issueServiceExtension,
                    issueServiceExtension.getConfigurationByName(identifier.getName())
            )).orElse(null);
        } else {
            return null;
        }
    }

    private IssueServiceConfiguration getIssueServiceConfigurationById(IssueServiceConfigurationIdentifier identifier) {
        Optional<IssueServiceExtension> issueService = getOptionalIssueService(identifier.getServiceId());
        return issueService.map(issueServiceExtension -> issueServiceExtension.getConfigurationByName(identifier.getName())).orElse(null);
    }
}
