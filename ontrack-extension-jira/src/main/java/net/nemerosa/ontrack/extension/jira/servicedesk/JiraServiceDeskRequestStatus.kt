package net.nemerosa.ontrack.extension.jira.servicedesk

/**
 * Which requests to look for when looking for an existing ticket.
 *
 * See https://docs.atlassian.com/jira-servicedesk/REST/5.15.1/#servicedeskapi/request-getMyCustomerRequests
 */
enum class JiraServiceDeskRequestStatus(
    val requestStatus: String,
) {

    CLOSED("CLOSED_REQUESTS"),
    OPEN("OPEN_REQUESTS"),
    ALL("ALL_REQUESTS")

}