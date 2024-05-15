package net.nemerosa.ontrack.extension.jira.servicedesk

import net.nemerosa.ontrack.extension.jira.model.JIRAIssueStub
import net.nemerosa.ontrack.extension.jira.notifications.JiraCustomField

interface JiraServiceDesk {

    fun searchRequest(
        serviceDeskId: Int,
        requestTypeId: Int,
        searchTerm: String,
        requestStatus: JiraServiceDeskRequestStatus,
    ): List<JIRAIssueStub>

    fun createRequest(
        serviceDeskId: Int,
        requestTypeId: Int,
        fields: List<JiraCustomField>
    ): JIRAIssueStub

}