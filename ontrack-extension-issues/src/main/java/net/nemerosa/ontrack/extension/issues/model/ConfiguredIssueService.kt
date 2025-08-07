package net.nemerosa.ontrack.extension.issues.model

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation.Companion.of
import net.nemerosa.ontrack.model.support.MessageAnnotator

/**
 * Association between an [net.nemerosa.ontrack.extension.issues.IssueServiceExtension] and
 * one of its [configuration][net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration]s.
 */
data class ConfiguredIssueService(
    val issueServiceExtension: IssueServiceExtension,
    val issueServiceConfiguration: IssueServiceConfiguration,
) {

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

    /**
     * Given an issue key, returns its display form.
     *
     * @param key Key ID
     * @return Display key
     */
    fun getDisplayKey(key: String): String {
        return issueServiceExtension.getDisplayKey(issueServiceConfiguration, key)
    }

}
