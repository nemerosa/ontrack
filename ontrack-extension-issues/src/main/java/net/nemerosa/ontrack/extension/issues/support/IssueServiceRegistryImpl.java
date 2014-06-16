package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.api.AvailableExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // FIXME Method net.nemerosa.ontrack.extension.issues.support.IssueServiceRegistryImpl.getOptionalIssueService
        return null;
    }
}
