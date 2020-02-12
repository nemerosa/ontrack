package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.MessageAnnotation
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.regex.Matcher

@Component
class MockIssueServiceExtension extends AbstractIssueServiceExtension {

    /**
     * Issues registered for testing
     */
    private final Map<Integer, MockIssue> issues = [:]

    @Autowired
    public MockIssueServiceExtension(MockIssueServiceFeature extensionFeature, IssueExportServiceFactory issueExportServiceFactory) {
        super(extensionFeature, "mock", "Mock issue", issueExportServiceFactory)
    }

    /**
     * Resets the list of registered issues
     */
    void resetIssues() {
        issues.clear()
    }

    /**
     * Registers some issues
     */
    void register(MockIssue... issues) {
        issues.each { this.issues.put(it.key as int, it) }
    }

    @Override
    protected Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        [issue.type as String] as Set
    }

    @Override
    List<? extends IssueServiceConfiguration> getConfigurationList() {
        [MockIssueServiceConfiguration.INSTANCE]
    }

    @Override
    IssueServiceConfiguration getConfigurationByName(String name) {
        if (name == MockIssueServiceConfiguration.INSTANCE.name) {
            MockIssueServiceConfiguration.INSTANCE
        } else {
            null
        }
    }

    @Override
    boolean validIssueToken(String token) {
        return token ==~ /#(\d+)/
    }

    @Override
    String getDisplayKey(IssueServiceConfiguration issueServiceConfiguration, String key) {
        if (key.startsWith("#")) {
            return key
        } else {
            return "#$key"
        }
    }

    @Override
    Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        Set<String> result = new HashSet<>()
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = (message =~ /#(\d+)/)
            while (matcher.find()) {
                // Gets the issue
                String issueKey = matcher.group(1);
                // Adds to the result
                result.add(issueKey);
            }
        }
        // OK
        return result;
    }

    @Override
    Optional<String> getIssueId(IssueServiceConfiguration issueServiceConfiguration, String token) {
        if (StringUtils.isNumeric(token) || validIssueToken(token)) {
            return Optional.of(String.valueOf(getIssueId(token)));
        } else {
            return Optional.empty();
        }
    }

    protected static int getIssueId(String issueKey) {
        return Integer.parseInt(StringUtils.stripStart(issueKey, "#"), 10);
    }

    @Override
    Optional<MessageAnnotator> getMessageAnnotator(IssueServiceConfiguration issueServiceConfiguration) {
        return Optional.of(
                new RegexMessageAnnotator(
                        /#(\d+)/,
                        { String token ->
                            MessageAnnotation.of('a')
                                    .attr('href', "http://issue/${token.substring(1)}")
                                    .text(token)
                        }
                )
        )
    }

    @Override
    String getLinkForAllIssues(IssueServiceConfiguration issueServiceConfiguration, List<Issue> issues) {
        null
    }

    @Override
    Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey) {
        def key = getIssueId(issueKey)
        issues.get(key) ?: new MockIssue(
                key,
                MockIssueStatus.OPEN,
                "bug"
        )
    }

    @Override
    Collection<? extends Issue> getLinkedIssues(Project project, IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        Map<Integer, MockIssue> issues = [:]
        collectLinkedIssues issue as MockIssue, issues
        issues.values()
    }

    static def collectLinkedIssues(MockIssue issue, Map<Integer, MockIssue> issues) {
        issues.put(issue.key as int, issue)
        issue.links.each { link ->
            if (!issues.containsKey(link.key as int)) {
                collectLinkedIssues link, issues
            }
        }
    }
}
