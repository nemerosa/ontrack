package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub

data class JiraCreationNotificationChannelOutput(
    val title: String? = null,
    val labels: List<String>? = null,
    val jql: String? = null,
    val existing: Boolean? = null,
    val fixVersion: String? = null,
    val customFields: List<JiraCustomField>? = null,
    val body: String? = null,
    val ticketKey: String? = null,
    val url: String? = null,
) {
    fun withLabels(labels: List<String>) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url,
    )

    fun withJql(jql: String) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )

    fun withExisting(existing: Boolean) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )

    fun withStub(stub: JIRAIssueStub) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, stub.key, stub.url
    )

    fun withTitle(title: String) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )

    fun withFixVersion(fixVersion: String?) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )

    fun withCustomFields(customFields: List<JiraCustomField>) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )

    fun withBody(body: String) = JiraCreationNotificationChannelOutput(
        title, labels, jql, existing, fixVersion, customFields, body, ticketKey, url
    )
}
