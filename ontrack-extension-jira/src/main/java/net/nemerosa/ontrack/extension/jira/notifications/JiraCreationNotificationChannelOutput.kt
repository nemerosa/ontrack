package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JiraCreationNotificationChannelOutput(
    @APIDescription("Actual summary of the ticket")
    val title: String? = null,
    @APIDescription("Actual labels of the ticket")
    val labels: List<String>? = null,
    @APIDescription("JQL query used to identify the existing ticket")
    val jql: String? = null,
    @APIDescription("True if the ticket was already existing")
    val existing: Boolean? = null,
    @APIDescription("Actual fix version assigned to the ticket")
    val fixVersion: String? = null,
    @APIDescription("Actual custom fields of the ticket")
    @DocumentationList
    val customFields: List<JiraCustomField>? = null,
    @APIDescription("Actual body for the ticket")
    val body: String? = null,
    @APIDescription("Ticket key")
    val ticketKey: String? = null,
    @APIDescription("URL to the ticket page")
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
