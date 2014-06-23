package net.nemerosa.ontrack.extension.jira.client;

import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;

public interface JIRAClient {

    JIRAIssue getIssue(String key);

}
