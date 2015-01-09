package net.nemerosa.ontrack.extension.jira.service;

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class JIRAServiceImpl implements JIRAService {

    private final JIRAClient client;

    @Autowired
    public JIRAServiceImpl(JIRAClient client) {
        this.client = client;
    }

    @Override
    public void followLinks(JIRAConfiguration configuration, JIRAIssue seed, Set<String> linkNames, Map<String, JIRAIssue> collectedIssues) {
        // Puts the seed into the list
        collectedIssues.put(seed.getKey(), seed);
        // Gets the linked issue keys
        seed.getLinks().stream()
                .filter(linkedIssue -> linkNames.contains(linkedIssue.getLinkName()))
                .filter(linkedIssue -> !collectedIssues.containsKey(linkedIssue.getKey()))
                .map(linkedIssue -> client.getIssue(linkedIssue.getKey(), configuration))
                .forEach(linkedIssue -> followLinks(configuration, linkedIssue, linkNames, collectedIssues));

    }
}
