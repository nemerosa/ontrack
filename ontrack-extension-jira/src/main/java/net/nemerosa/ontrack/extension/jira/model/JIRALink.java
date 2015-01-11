package net.nemerosa.ontrack.extension.jira.model;

import lombok.Data;

@Data
public class JIRALink {

    private final String key;
    private final String url;
    private final JIRAStatus status;
    private final String linkName;
    private final String link;

}
