package net.nemerosa.ontrack.extension.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.ClientForbiddenException;
import net.nemerosa.ontrack.client.ClientNotFoundException;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.model.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JIRAClientImpl implements JIRAClient {

    public static final DateTimeFormatter JIRA_DATA_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private final JsonClient jsonClient;

    public JIRAClientImpl(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public JIRAIssue getIssue(String key, JIRAConfiguration configuration) {
        JsonNode node;
        try {
            node = jsonClient.get("/rest/api/2/issue/%s?expand=names", key);
        } catch (ClientForbiddenException ex) {
            // The issue cannot be accessed
            // TODO What do we log here?
            // For the moment, ignoring silently
            return null;
        } catch (ClientNotFoundException ex) {
            // Issue not found
            return null;
        }
        return toIssue(configuration, node);


    }

    @Override
    public List<String> getProjects() {
        JsonNode node = jsonClient.get("/rest/api/2/project");
        List<String> projects = new ArrayList<>();
        for (JsonNode child : node) {
            projects.add(child.path("key").asText());
        }
        return projects;
    }

    protected static JIRAIssue toIssue(JIRAConfiguration configuration, JsonNode node) {
        // Translation of fields
        List<JIRAField> fields = new ArrayList<>();
        JsonNode names = node.path("names");
        Iterator<Map.Entry<String, JsonNode>> nameFields = names.fields();
        while (nameFields.hasNext()) {
            Map.Entry<String, JsonNode> nameField = nameFields.next();
            String name = nameField.getKey();
            String displayName = nameField.getValue().asText();
            // Gets the field node
            JsonNode fieldNode = field(node, name);
            // Creates the field
            fields.add(
                    new JIRAField(
                            name,
                            displayName,
                            fieldNode
                    )
            );
        }

        // Versions
        List<JIRAVersion> affectedVersions = toVersions(node, "versions");
        List<JIRAVersion> fixVersions = toVersions(node, "fixVersions");

        // Status
        JIRAStatus status = getStatus(node);

        // Key
        String key = node.path("key").asText();

        // Issue links
        List<JIRALink> links = new ArrayList<>();
        for (JsonNode issueLinkNode : field(node, "issuelinks")) {
            String inwardKey = issueLinkNode.path("inwardIssue").path("key").asText();
            String outwardKey = issueLinkNode.path("outwardIssue").path("key").asText();
            if (StringUtils.isNotBlank(inwardKey)) {
                links.add(new JIRALink(
                        inwardKey,
                        configuration.getIssueURL(inwardKey),
                        getStatus(issueLinkNode.path("inwardIssue")),
                        issueLinkNode.path("type").path("name").asText(),
                        issueLinkNode.path("type").path("inward").asText()
                ));
            } else if (StringUtils.isNotBlank(outwardKey)) {
                links.add(new JIRALink(
                        outwardKey,
                        configuration.getIssueURL(outwardKey),
                        getStatus(issueLinkNode.path("outwardIssue")),
                        issueLinkNode.path("type").path("name").asText(),
                        issueLinkNode.path("type").path("outward").asText()
                ));
            }
        }

        // JIRA issue
        return new JIRAIssue(
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
        );
    }

    private static JIRAStatus getStatus(JsonNode node) {
        JsonNode statusNode = field(node, "status");
        return new JIRAStatus(
                statusNode.path("name").asText(),
                statusNode.path("iconUrl").asText()
        );
    }

    public static LocalDateTime parseFromJIRA(String value) {
        return LocalDateTime.ofInstant(
                ZonedDateTime.parse(value, JIRA_DATA_TIME).toInstant(),
                ZoneOffset.UTC
        );
    }

    private static List<JIRAVersion> toVersions(JsonNode node, String versionFieldName) {
        JsonNode versionField = field(node, versionFieldName);
        List<JIRAVersion> versions = new ArrayList<>();
        for (JsonNode versionNode : versionField) {
            versions.add(
                    new JIRAVersion(
                            versionNode.path("name").asText(),
                            versionNode.path("released").asBoolean()
                    )
            );
        }
        return versions;
    }

    private static String fieldValue(JsonNode node, String name) {
        return field(node, name).asText();
    }

    private static JsonNode field(JsonNode node, String name) {
        return node.path("fields").path(name);
    }

    @Override
    public void close() {
    }
}
