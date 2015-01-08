package net.nemerosa.ontrack.extension.jira.client;

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;

import java.util.Map;
import java.util.Set;

public interface JIRAClient {

    JIRAIssue getIssue(String key, JIRAConfiguration configuration);

    /**
     * Given an issue seed, and a list of link names, follows the given links recursively and
     * puts the associated issues into the {@code collectedIssues} map.
     *
     * @param configuration   JIRA configuration to use to load the issues
     * @param seed            Issue to start from.
     * @param linkNames       Links to follow
     * @param collectedIssues Collected issues, indexed by their key
     */
    void followLinks(JIRAConfiguration configuration, JIRAIssue seed, Set<String> linkNames, Map<String, JIRAIssue> collectedIssues);

    void close();
}
