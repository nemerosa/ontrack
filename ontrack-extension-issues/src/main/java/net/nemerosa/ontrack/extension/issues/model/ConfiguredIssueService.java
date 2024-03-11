package net.nemerosa.ontrack.extension.issues.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        MessageAnnotator messageAnnotator = issueServiceExtension.getMessageAnnotator(issueServiceConfiguration);
        if (messageAnnotator != null) {
            return MessageAnnotationUtils.annotate(message, Collections.singletonList(messageAnnotator));
        } else {
            return "";
        }
    }

    @Nullable
    public Issue getIssue(String issueKey) {
        return issueServiceExtension.getIssue(issueServiceConfiguration, issueKey);
    }

    public IssueServiceConfigurationRepresentation getIssueServiceConfigurationRepresentation() {
        return IssueServiceConfigurationRepresentation.Companion.of(
                issueServiceExtension,
                issueServiceConfiguration
        );
    }

    public MessageAnnotator getMessageAnnotator() {
        return issueServiceExtension.getMessageAnnotator(issueServiceConfiguration);
    }

    public Set<String> extractIssueKeysFromMessage(String message) {
        return issueServiceExtension.extractIssueKeysFromMessage(issueServiceConfiguration, message);
    }

    public @Nullable String getIssueId(@NotNull String token) {
        return issueServiceExtension.getIssueId(issueServiceConfiguration, token);
    }

    /**
     * Given an issue key, returns its display form.
     *
     * @param key Key ID
     * @return Display key
     */
    public String getDisplayKey(String key) {
        return issueServiceExtension.getDisplayKey(issueServiceConfiguration, key);
    }

    @NotNull
    public String getMessageRegex(@NotNull Issue issue) {
        return issueServiceExtension.getMessageRegex(issueServiceConfiguration, issue);
    }
}
