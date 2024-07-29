package net.nemerosa.ontrack.extension.issues.model

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.model.support.MessageAnnotator

/**
 * Association between an [net.nemerosa.ontrack.extension.issues.IssueServiceExtension] and
 * one of its [configuration][net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration]s.
 */
data class ConfiguredIssueService(
    val issueServiceExtension: IssueServiceExtension,
    val issueServiceConfiguration: IssueServiceConfiguration,
) {

    fun formatIssuesInMessage(message: String?): String {
        val messageAnnotator = issueServiceExtension.getMessageAnnotator(issueServiceConfiguration)
        return if (messageAnnotator != null) {
            MessageAnnotationUtils.annotate(message, listOf(messageAnnotator))
        } else {
            ""
        }
    }

    fun getIssue(issueKey: String): Issue? {
        return issueServiceExtension.getIssue(issueServiceConfiguration, issueKey)
    }

    val issueServiceConfigurationRepresentation: IssueServiceConfigurationRepresentation
        get() = of(
            issueServiceExtension,
            issueServiceConfiguration
        )

    val messageAnnotator: MessageAnnotator?
        get() = issueServiceExtension.getMessageAnnotator(issueServiceConfiguration)

    fun extractIssueKeysFromMessage(message: String): Set<String> {
        return issueServiceExtension.extractIssueKeysFromMessage(issueServiceConfiguration, message)
    }

    fun getIssueId(token: String): String? {
        return issueServiceExtension.getIssueId(issueServiceConfiguration, token)
    }

    /**
     * Given an issue key, returns its display form.
     *
     * @param key Key ID
     * @return Display key
     */
    fun getDisplayKey(key: String): String {
        return issueServiceExtension.getDisplayKey(issueServiceConfiguration, key)
    }

    fun getMessageRegex(issue: Issue): String {
        return issueServiceExtension.getMessageRegex(issueServiceConfiguration, issue)
    }
}
