package net.nemerosa.ontrack.extension.issues;

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.export.ExportFormat;
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.model.extension.Extension;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Defines a generic service used to access issues from a ticketing system like
 * JIRA, GitHub, etc.
 */
public interface IssueServiceExtension extends Extension {

    /**
     * Gets the ID of this service. It must be unique among all the available
     * issue services. This must be mapped to the associated extension since this
     * ID can be used to identify web resources using the <code>/extension/&lt;id&gt;</code> URI.
     */
    String getId();

    /**
     * Gets the display name for this service.
     */
    String getName();

    /**
     * Returns the unfiltered list of all configurations for this issue service.
     */
    List<? extends IssueServiceConfiguration> getConfigurationList();

    /**
     * Gets a configuration using its name
     *
     * @param name Name of the configuration
     * @return Configuration or <code>null</code> if not found
     */
    @Nullable IssueServiceConfiguration getConfigurationByName(@NotNull String name);

    /**
     * Given a message, extracts the issue keys from the message
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param message                   Message to scan
     * @return List of keys (can be empty, never <code>null</code>)
     */
    Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message);

    /**
     * Returns a message annotator that can be used to extract information from a commit message. According
     * to the service type and its configuration, they could be or not a possible annotator.
     */
    @Nullable MessageAnnotator getMessageAnnotator(@NotNull IssueServiceConfiguration issueServiceConfiguration);

    /**
     * Given a key, tries to find the issue with this key.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issueKey                  Issue key
     * @return Issue if found, <code>null</code> otherwise
     */
    @Nullable
    Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey);

    /**
     * List of supported export formats for the issues.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @deprecated Export formats are no longer issue service specific - will be removed in V5
     */
    List<ExportFormat> exportFormats(IssueServiceConfiguration issueServiceConfiguration);

    /**
     * Exports a list of issues as text for a given <code>format</code>.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issues                    List of issues to export
     * @param request                   Specification for the export
     * @throws net.nemerosa.ontrack.extension.issues.model.IssueExportFormatNotFoundException If the format is not supported.
     * @deprecated Will be removed in V5. Use the template service instead.
     */
    @Deprecated
    ExportedIssues exportIssues(IssueServiceConfiguration issueServiceConfiguration, List<? extends Issue> issues, IssueChangeLogExportRequest request);

    /**
     * Normalises a string into a valid issue key if possible, in order for it to be useable in a search. This allows
     * for services to adjust the token for cases where the <i>representation</i> of an issue might be different
     * from its actual <i>indexed</i> value. For example, in GitHub, the representation might <code>#12</code>
     * while the value to search on is <code>12</code>.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param token                     Token to transform into a key
     * @return Valid token or null.
     */
    @Nullable
    String getIssueId(IssueServiceConfiguration issueServiceConfiguration, String token);

    /**
     * Creates a regular expression to use when looking for this issue in a message.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issue                     Issue to look for
     * @return Regular expression
     */
    String getMessageRegex(IssueServiceConfiguration issueServiceConfiguration, Issue issue);


    /**
     * Given an issue key, returns its display form.
     * <p>
     * By default, returns the <code>key</code>.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param key                       Key ID
     * @return Display key
     */
    default String getDisplayKey(IssueServiceConfiguration issueServiceConfiguration, String key) {
        return key;
    }

    @NotNull Set<String> getIssueTypes(@NotNull IssueServiceConfiguration issueServiceConfiguration, @NotNull Issue issue);
}
