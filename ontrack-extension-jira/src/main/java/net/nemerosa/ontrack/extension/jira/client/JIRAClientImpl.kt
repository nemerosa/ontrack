package net.nemerosa.ontrack.extension.jira.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.client.ClientForbiddenException
import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.*
import org.apache.commons.lang3.StringUtils
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class JIRAClientImpl(private val jsonClient: JsonClient) : JIRAClient {

    override fun getIssue(key: String, configuration: JIRAConfiguration): JIRAIssue? {
        val node: JsonNode
        try {
            node = jsonClient.get("/rest/api/2/issue/%s?expand=names", key)
        } catch (ex: ClientForbiddenException) {
            // The issue cannot be accessed
            // For the moment, ignoring silently
            return null
        } catch (ex: ClientNotFoundException) {
            return null
        }

        return toIssue(configuration, node)
    }

    override fun getProjects(): List<String> {
        val node = jsonClient.get("/rest/api/2/project")
        val projects = ArrayList<String>()
        for (child in node) {
            projects.add(child.path("key").asText())
        }
        return projects
    }

    override fun close() {}

    companion object {

        private val JIRA_DATA_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

        private fun toIssue(configuration: JIRAConfiguration, node: JsonNode): JIRAIssue {
            // Translation of fields
            val fields = ArrayList<JIRAField>()
            val names = node.path("names")
            val nameFields = names.fields()
            while (nameFields.hasNext()) {
                val nameField = nameFields.next()
                val name = nameField.key
                val displayName = nameField.value.asText()
                // Gets the field node
                val fieldNode = field(node, name)
                // Creates the field
                fields.add(
                        JIRAField(
                                name,
                                displayName,
                                fieldNode
                        )
                )
            }

            // Versions
            val affectedVersions = toVersions(node, "versions")
            val fixVersions = toVersions(node, "fixVersions")

            // Status
            val status = getStatus(node)

            // Key
            val key = node.path("key").asText()

            // Issue links
            val links = ArrayList<JIRALink>()
            for (issueLinkNode in field(node, "issuelinks")) {
                val inwardKey = issueLinkNode.path("inwardIssue").path("key").asText()
                val outwardKey = issueLinkNode.path("outwardIssue").path("key").asText()
                if (StringUtils.isNotBlank(inwardKey)) {
                    links.add(JIRALink(
                            inwardKey,
                            configuration.getIssueURL(inwardKey),
                            getStatus(issueLinkNode.path("inwardIssue")),
                            issueLinkNode.path("type").path("name").asText(),
                            issueLinkNode.path("type").path("inward").asText()
                    ))
                } else if (StringUtils.isNotBlank(outwardKey)) {
                    links.add(JIRALink(
                            outwardKey,
                            configuration.getIssueURL(outwardKey),
                            getStatus(issueLinkNode.path("outwardIssue")),
                            issueLinkNode.path("type").path("name").asText(),
                            issueLinkNode.path("type").path("outward").asText()
                    ))
                }
            }

            // JIRA issue
            return JIRAIssue(
                    configuration.getIssueURL(key),
                    key,
                    fieldValue(node, "summary"),
                    status,
                    field(node, "assignee").path("name").asText(),
                    parseFromJIRA(fieldValue(node, "updated")),
                    fields,
                    affectedVersions,
                    fixVersions,
                    field(node, "issuetype").path("name").asText(),
                    links
            )
        }

        private fun getStatus(node: JsonNode): JIRAStatus {
            val statusNode = field(node, "status")
            return JIRAStatus(
                    statusNode.path("name").asText(),
                    statusNode.path("iconUrl").asText()
            )
        }

        private fun parseFromJIRA(value: String): LocalDateTime {
            return LocalDateTime.ofInstant(
                    ZonedDateTime.parse(value, JIRA_DATA_TIME).toInstant(),
                    ZoneOffset.UTC
            )
        }

        private fun toVersions(node: JsonNode, versionFieldName: String): List<JIRAVersion> {
            val versionField = field(node, versionFieldName)
            val versions = ArrayList<JIRAVersion>()
            for (versionNode in versionField) {
                versions.add(
                        JIRAVersion(
                                versionNode.path("name").asText(),
                                versionNode.path("released").asBoolean()
                        )
                )
            }
            return versions
        }

        private fun fieldValue(node: JsonNode, name: String): String {
            return field(node, name).asText()
        }

        private fun field(node: JsonNode, name: String): JsonNode {
            return node.path("fields").path(name)
        }
    }
}
