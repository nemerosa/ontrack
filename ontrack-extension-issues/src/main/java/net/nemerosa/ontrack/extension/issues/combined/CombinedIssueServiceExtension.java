package net.nemerosa.ontrack.extension.issues.combined;

import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CombinedIssueServiceExtension extends AbstractIssueServiceExtension {

    public static final String SERVICE = "combined";

    @Autowired
    public CombinedIssueServiceExtension(CombinedIssueServiceExtensionFeature extensionFeature, String id, String name, IssueExportServiceFactory issueExportServiceFactory) {
        super(extensionFeature, SERVICE, "Combined issue service", issueExportServiceFactory);
    }

    @Override
    protected Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getIssueTypes
        return null;
    }

    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getConfigurationList
        return null;
    }

    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getConfigurationByName
        return null;
    }

    @Override
    public boolean validIssueToken(String token) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.validIssueToken
        return false;
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.extractIssueKeysFromMessage
        return null;
    }

    @Override
    public Optional<MessageAnnotator> getMessageAnnotator(IssueServiceConfiguration issueServiceConfiguration) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getMessageAnnotator
        return null;
    }

    @Override
    public String getLinkForAllIssues(IssueServiceConfiguration issueServiceConfiguration, List<Issue> issues) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getLinkForAllIssues
        return null;
    }

    @Override
    public Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getIssue
        return null;
    }

    @Override
    public Optional<String> getIssueId(IssueServiceConfiguration issueServiceConfiguration, String token) {
        // FIXME Method net.nemerosa.ontrack.extension.issues.combined.CombinedIssueServiceExtension.getIssueId
        return null;
    }
}
