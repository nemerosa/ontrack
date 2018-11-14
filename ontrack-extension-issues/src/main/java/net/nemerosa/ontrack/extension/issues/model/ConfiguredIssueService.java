package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils;
import net.nemerosa.ontrack.model.support.MessageAnnotator;

import java.util.*;

/**
 * Association between an {@link net.nemerosa.ontrack.extension.issues.IssueServiceExtension} and
 * one of its {@link net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration configuration}s.
 */
@Data
public class ConfiguredIssueService {

    private final IssueServiceExtension issueServiceExtension;
    private final IssueServiceConfiguration issueServiceConfiguration;

    public String formatIssuesInMessage(String message) {
        return issueServiceExtension.getMessageAnnotator(issueServiceConfiguration)
                .map(annotator -> MessageAnnotationUtils.annotate(message, Collections.singletonList(annotator)))
                .orElse("");
    }

    public String getLinkForAllIssues(List<Issue> issues) {
        return issueServiceExtension.getLinkForAllIssues(issueServiceConfiguration, issues);
    }

    public Issue getIssue(String issueKey) {
        return issueServiceExtension.getIssue(issueServiceConfiguration, issueKey);
    }

    public IssueServiceConfigurationRepresentation getIssueServiceConfigurationRepresentation() {
        return IssueServiceConfigurationRepresentation.Companion.of(
                issueServiceExtension,
                issueServiceConfiguration
        );
    }

    public Optional<MessageAnnotator> getMessageAnnotator() {
        return issueServiceExtension.getMessageAnnotator(issueServiceConfiguration);
    }

    /**
     * @deprecated Do not use
     */
    @Deprecated
    public Set<String> extractIssueKeysFromMessage(String message) {
        return issueServiceExtension.extractIssueKeysFromMessage(issueServiceConfiguration, message);
    }

    /**
     * @deprecated Do not use
     */
    @Deprecated
    public boolean containsIssueKey(String key, Set<String> keys) {
        return issueServiceExtension.containsIssueKey(issueServiceConfiguration, key, keys);
    }

    public Optional<String> getIssueId(String token) {
        return issueServiceExtension.getIssueId(issueServiceConfiguration, token);
    }

    public Collection<? extends Issue> getLinkedIssues(Project project, Issue issue) {
        return issueServiceExtension.getLinkedIssues(project, issueServiceConfiguration, issue);
    }
}
