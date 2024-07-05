package net.nemerosa.ontrack.kdsl.spec.extension.jira.mock

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parseOrNull
import net.nemerosa.ontrack.kdsl.spec.extension.jira.JiraIssue

class JiraMockMgt(connector: Connector) : Connected(connector) {

    private fun findIssueByKey(key: String): JiraIssue? =
        connector.get("/extension/jira/mock/issue/$key").body.parseOrNull()

    fun getIssueByKey(key: String): JiraIssue =
        findIssueByKey(key) ?: error("Could not find issue $key")
}