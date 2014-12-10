package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension
import net.nemerosa.ontrack.model.support.MessageAnnotation
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.regex.Matcher

@Component
class MockIssueServiceExtension extends AbstractIssueServiceExtension {

    @Autowired
    public MockIssueServiceExtension(MockIssueServiceFeature extensionFeature, IssueExportServiceFactory issueExportServiceFactory) {
        super(extensionFeature, "mock", "Mock issue", issueExportServiceFactory)
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
        token != null && /#\d+/ ==~ token
    }

    @Override
    Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        Set<String> result = new HashSet<>()
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = /#(\d+)/ =~ message
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
        new MockIssue(
                issueKey as int,
                MockIssueStatus.OPEN,
                "bug"
        )
    }
}
