package net.nemerosa.ontrack.extension.jira.client;

import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;

public class JIRAClientImpl implements JIRAClient {

    private final JsonClient jsonClient;

    public JIRAClientImpl(JsonClient jsonClient) {
        this.jsonClient = jsonClient;
    }

    @Override
    public JIRAIssue getIssue(String key) {

        // TODO Translation of fields
//                List<JIRAField> fields = issue.get
//                        Lists.newArrayList(
//                        Iterables.transform(
//                                issue.getFields(),
//                                new Function<IssueField, JIRAField>() {
//                                    @Override
//                                    public JIRAField apply(IssueField f) {
//                                        return toField(f);
//                                    }
//                                }
//                        )
//                );

        // TODO Versions
//                List<JIRAVersion> affectedVersions = toVersions(issue.getAffectedVersions());
//                List<JIRAVersion> fixVersions = toVersions(issue.getFixVersions());

        // TODO Status
//                JIRAStatus status = toStatus(configuration, issue.getStatus());

        // TODO JIRA issue
//                return new JIRAIssue(
//                        getIssueURL(configuration, issue.getKey()),
//                        issue.getKey(),
//                        issue.getSummary(),
//                        status,
//                        getUserName(issue.getAssignee()),
//                        issue.getUpdateDate(),
//                        fields,
//                        affectedVersions,
//                        fixVersions
//                );
        // FIXME Method net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl.getIssue
        return null;
    }

    @Override
    public void close() {
    }
}
