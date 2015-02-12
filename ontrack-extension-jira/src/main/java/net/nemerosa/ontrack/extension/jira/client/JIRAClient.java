package net.nemerosa.ontrack.extension.jira.client;

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;

import java.util.List;

public interface JIRAClient extends AutoCloseable {

    JIRAIssue getIssue(String key, JIRAConfiguration configuration);

    List<String> getProjects();

    void close();

}
