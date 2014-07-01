package net.nemerosa.ontrack.extension.issues;

import net.nemerosa.ontrack.extension.api.Extension;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

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
    IssueServiceConfiguration getConfigurationByName(String name);

    /**
     * Checks if a token may represent a valid issue token.
     *
     * @param token Token to test
     * @return <code>true</code> if the token may represent an issue
     */
    boolean validIssueToken(String token);

    /**
     * Given a message, extracts the issue keys from the message
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param message                   Message to scan
     * @return List of keys (can be empty, never <code>null</code>)
     */
    Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message);

    /**
     * Given a message, returns this message where issues have been replaced
     * by hyperlinks to a page that can display details about this issue.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param message                   Message to format
     * @return Formatted message
     */
    String formatIssuesInMessage(IssueServiceConfiguration issueServiceConfiguration, String message);

    /**
     * Given a list of issues, returns a link that allows the user to display the list of
     * all those issues in a browser.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issues                    List of issues to display. Can be empty, but not <code>null</code>.
     * @return Link
     */
    String getLinkForAllIssues(IssueServiceConfiguration issueServiceConfiguration, List<Issue> issues);

    /**
     * Given a key, tries to find the issue with this key.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issueKey                  Issue key
     * @return Issue if found, <code>null</code> otherwise
     */
    Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey);
}
