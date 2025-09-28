package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.support.MessageAnnotation
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator
import net.nemerosa.ontrack.tx.Transaction
import net.nemerosa.ontrack.tx.TransactionResourceProvider
import net.nemerosa.ontrack.tx.TransactionService
import org.springframework.stereotype.Component

@Component
class JIRAServiceExtension(
    extensionFeature: JIRAExtensionFeature,
    private val jiraConfigurationService: JIRAConfigurationService,
    private val jiraSessionFactory: JIRASessionFactory,
    private val transactionService: TransactionService,
    private val propertyService: PropertyService
) : AbstractIssueServiceExtension(extensionFeature, SERVICE, "JIRA") {

    override fun getConfigurationList(): List<IssueServiceConfiguration> {
        return jiraConfigurationService.configurations
    }

    override fun getConfigurationByName(name: String): IssueServiceConfiguration? {
        return jiraConfigurationService.findConfiguration(name)
    }

    override fun getIssueId(issueServiceConfiguration: IssueServiceConfiguration, token: String): String? =
        if (issueServiceConfiguration is JIRAConfiguration && issueServiceConfiguration.isValidIssueKey(token)) {
            token
        } else {
            null
        }

    override fun extractIssueKeysFromMessage(
        issueServiceConfiguration: IssueServiceConfiguration,
        message: String
    ): Set<String> {
        return extractJIRAIssuesFromMessage(issueServiceConfiguration as JIRAConfiguration, message)
    }

    override fun getMessageAnnotator(issueServiceConfiguration: IssueServiceConfiguration): MessageAnnotator {
        val configuration = issueServiceConfiguration as JIRAConfiguration
        return RegexMessageAnnotator(
            JIRAConfiguration.ISSUE_PATTERN_REGEX
        ) { key: String ->
            MessageAnnotation.of("a")
                .attr("href", configuration.getIssueURL(key))
                .text(key)
        }
    }

    override fun getIssue(issueServiceConfiguration: IssueServiceConfiguration, issueKey: String): Issue? {
        return getIssue(issueServiceConfiguration as JIRAConfiguration, issueKey)
    }

    override fun getIssueTypes(issueServiceConfiguration: IssueServiceConfiguration, issue: Issue): Set<String> {
        val jiraIssue = issue as JIRAIssue
        return setOf(jiraIssue.issueType)
    }

    /**
     * Given an issue seed, and a list of link names, follows the given links recursively and
     * puts the associated issues into the `collectedIssues` map.
     *
     * @param configuration   JIRA configuration to use to load the issues
     * @param seed            Issue to start from.
     * @param linkNames       Links to follow
     * @param collectedIssues Collected issues, indexed by their key
     */
    fun followLinks(
        configuration: JIRAConfiguration,
        seed: JIRAIssue,
        linkNames: Set<String?>,
        collectedIssues: MutableMap<String, JIRAIssue>
    ) {
        transactionService.start().use { tx ->
            val session = getJIRASession(tx, configuration)
            // Gets the client from the current session
            val client = session.client
            // Puts the seed into the list
            collectedIssues[seed.key] = seed
            // Gets the linked issue keys
            seed.links.stream()
                .filter { linkedIssue: JIRALink -> linkNames.contains(linkedIssue.linkName) }
                .filter { linkedIssue: JIRALink -> !collectedIssues.containsKey(linkedIssue.key) }
                .map { linkedIssue: JIRALink -> client.getIssue(linkedIssue.key, configuration) }
                .forEach { linkedIssue: JIRAIssue? ->
                    if (linkedIssue != null) {
                        followLinks(
                            configuration,
                            linkedIssue,
                            linkNames,
                            collectedIssues
                        )
                    }
                }
        }
    }

    fun getIssue(configuration: JIRAConfiguration, key: String): JIRAIssue? {
        transactionService.start().use { tx ->
            val session = getJIRASession(tx, configuration)
            // Gets the JIRA issue
            return session.client.getIssue(key, configuration)
        }
    }

    private fun getJIRASession(tx: Transaction, configuration: JIRAConfiguration): JIRASession =
        tx.getResource(
            JIRASession::class.java,
            configuration.name,
            object : TransactionResourceProvider<JIRASession> {
                override fun createTxResource(): JIRASession = jiraSessionFactory.create(configuration)
            }
        )

    protected fun extractJIRAIssuesFromMessage(configuration: JIRAConfiguration, message: String): Set<String> {
        val result = mutableSetOf<String>()
        if (message.isNotBlank()) {
            val matchers = JIRAConfiguration.ISSUE_PATTERN_REGEX.findAll(message)
            matchers.forEach { matcher ->
                val issueKey = matcher.groupValues[1]
                if (configuration.isValidIssueKey(issueKey)) {
                    result += issueKey
                }
            }
        }
        // OK
        return result
    }

    override fun getLastCommit(
        issueServiceConfiguration: IssueServiceConfiguration,
        key: String
    ): String? {
        val configuration = issueServiceConfiguration as JIRAConfiguration
        transactionService.start().use { tx ->
            val session = getJIRASession(tx, configuration)
            // Gets the JIRA issue
            return session.client.getIssueLastCommit(key, configuration)
        }
    }

    companion object {
        const val SERVICE: String = "jira"
    }
}
