package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;

import java.util.List;

/**
 * Association between an {@link net.nemerosa.ontrack.extension.issues.IssueServiceExtension} and
 * one of its {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration configuration}s.
 */
@Data
public class ConfiguredIssueService {

    private final IssueServiceExtension issueServiceExtension;
    private final IssueServiceConfiguration issueServiceConfiguration;

    public String formatIssuesInMessage(String message) {
        return issueServiceExtension.formatIssuesInMessage(issueServiceConfiguration, message);
    }

    public String getLinkForAllIssues(List<Issue> issues) {
        return issueServiceExtension.getLinkForAllIssues(issueServiceConfiguration, issues);
    }

    public Issue getIssue(String issueKey) {
        return issueServiceExtension.getIssue(issueServiceConfiguration, issueKey);
    }

    public IssueServiceConfigurationRepresentation getIssueServiceConfigurationRepresentation() {
        return IssueServiceConfigurationRepresentation.of(
                issueServiceExtension,
                issueServiceConfiguration
        );
    }
}
