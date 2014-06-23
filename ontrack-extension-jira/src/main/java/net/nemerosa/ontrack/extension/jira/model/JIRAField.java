package net.nemerosa.ontrack.extension.jira.model;

import lombok.Data;

@Data
public class JIRAField {

    private final String name;
    private final String type;
    private final String value;

}
