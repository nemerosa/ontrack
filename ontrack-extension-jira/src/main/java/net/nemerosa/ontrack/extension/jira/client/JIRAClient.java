package net.nemerosa.ontrack.extension.jira.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface JIRAClient {

    JsonNode getIssue(String key);

}
