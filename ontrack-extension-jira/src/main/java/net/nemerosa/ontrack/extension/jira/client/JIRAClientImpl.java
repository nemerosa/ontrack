package net.nemerosa.ontrack.extension.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.model.JIRAField;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus;
import net.nemerosa.ontrack.extension.jira.model.JIRAVersion;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JIRAClientImpl implements JIRAClient {

    private final JsonClient jsonClient;

    public JIRAClientImpl(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public JIRAIssue getIssue(String key, JIRAConfiguration configuration) {
        JsonNode node = jsonClient.get("/rest/api/2/issue/%s?expand=names", key);

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
        List<JIRAVersion> fixVersions = toVersions(node, "fixVersion");

        // Status
        JsonNode statusNode = field(node, "status");
        JIRAStatus status = new JIRAStatus(
                fieldValue(statusNode, "name"),
                fieldValue(statusNode, "iconUrl")
        );

        // JIRA issue
        return new JIRAIssue(
                configuration.getIssueURL(key),
                key,
                fieldValue(node, "summary"),
                status,
                field(node, "assignee").path("name").asText(),
                LocalDateTime.parse(fieldValue(node, "updated")),
                fields,
                affectedVersions,
                fixVersions
        );
    }

    private List<JIRAVersion> toVersions(JsonNode node, String versionFieldName) {
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

    private String fieldValue(JsonNode node, String name) {
        return field(node, name).asText();
    }

    private JsonNode field(JsonNode node, String name) {
        return node.path("fields").path(name);
    }

    @Override
    public void close() {
    }
}
