package net.nemerosa.ontrack.extension.jira.model;

import lombok.Data;

@Data
public class JIRAVersion {

    private final String name;
    private final boolean released;

}
