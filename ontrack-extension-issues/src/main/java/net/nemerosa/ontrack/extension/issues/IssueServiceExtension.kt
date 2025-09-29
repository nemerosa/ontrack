package net.nemerosa.ontrack.extension.issues

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.support.MessageAnnotator

/**
 * Defines a generic service used to access issues from a ticketing system like
 * JIRA, GitHub, etc.
 */
interface IssueServiceExtension : Extension {

    /**
     * Gets the ID of this service. It must be unique among all the available
     * issue services. This must be mapped to the associated extension since this
     * ID can be used to identify web resources using the `/extension/<id>` URI.
     */
    val id: String

    /**
     * Gets the display name for this service.
     */
    val name: String

    /**
     * Returns the unfiltered list of all configurations for this issue service.
     */
    fun getConfigurationList(): List<IssueServiceConfiguration>

    /**
     * Gets a configuration using its name
     *
     * @param name Name of the configuration
     * @return Configuration or `null` if not found
     */
    fun getConfigurationByName(name: String): IssueServiceConfiguration?

    /**
     * Given a message, extracts the issue keys from the message
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param message                   Message to scan
     * @return List of keys (can be empty, never `null`)
     */
    fun extractIssueKeysFromMessage(
        issueServiceConfiguration: IssueServiceConfiguration,
        message: String?
    ): Set<String>

    /**
     * Returns a message annotator that can be used to extract information from a commit message. According
     * to the service type and its configuration, they could be or not a possible annotator.
     */
    fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): MessageAnnotator

    /**
     * Given a key, tries to find the issue with this key.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param issueKey                  Issue key
     * @return Issue if found, `null` otherwise
     */
    fun getIssue(
        issueServiceConfiguration: IssueServiceConfiguration,
        issueKey: String
    ): Issue?

    /**
     * Normalises a string into a valid issue key if possible, in order for it to be useable in a search. This allows
     * for services to adjust the token for cases where the *representation* of an issue might be different
     * from its actual *indexed* value. For example, in GitHub, the representation might `#12`
     * while the value to search on is `12`.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param token                     Token to transform into a key
     * @return Valid token or null.
     */
    fun getIssueId(
        issueServiceConfiguration: IssueServiceConfiguration,
        token: String?
    ): String?

    /**
     * Given an issue key, returns its display form.
     *
     *
     * By default, returns the `key`.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param key                       Key ID
     * @return Display key
     */
    fun getDisplayKey(
        issueServiceConfiguration: IssueServiceConfiguration,
        key: String
    ): String {
        return key
    }

    fun getIssueTypes(issueServiceConfiguration: IssueServiceConfiguration, issue: Issue): Set<String>

    /**
     * Given an issue key, returns, if any, its last associated commit.
     *
     * @param issueServiceConfiguration Configuration for the service
     * @param repositoryContext SCM repository context
     * @param key                       Key ID
     * @return Commit ID or null if none.
     */
    fun getLastCommit(
        issueServiceConfiguration: IssueServiceConfiguration,
        repositoryContext: IssueRepositoryContext,
        key: String,
    ): String?
}
