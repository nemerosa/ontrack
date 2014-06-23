package net.nemerosa.ontrack.extension.jira.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class JIRAField {

    private final String id;
    private final String name;
    private final JsonNode value;

}
