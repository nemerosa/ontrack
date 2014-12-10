package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.model.support.MessageAnnotation;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class JIRAServiceExtension extends AbstractIssueServiceExtension {

    public static final String SERVICE = "jira";
    private final JIRAConfigurationService jiraConfigurationService;
    private final JIRASessionFactory jiraSessionFactory;
    private final TransactionService transactionService;

    @Autowired
    public JIRAServiceExtension(
            JIRAExtensionFeature extensionFeature,
            JIRAConfigurationService jiraConfigurationService,
            JIRASessionFactory jiraSessionFactory,
            TransactionService transactionService,
            IssueExportServiceFactory issueExportServiceFactory
    ) {
        super(extensionFeature, SERVICE, "JIRA", issueExportServiceFactory);
        this.jiraConfigurationService = jiraConfigurationService;
        this.jiraSessionFactory = jiraSessionFactory;
        this.transactionService = transactionService;
    }

    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        return jiraConfigurationService.getConfigurations();
    }

    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        return jiraConfigurationService.getOptionalConfiguration(name).orElse(null);
    }

    @Override
    public Optional<String> getIssueId(IssueServiceConfiguration issueServiceConfiguration, String token) {
        return validIssueToken(token) ? Optional.of(token) : Optional.empty();
    }

    @Override
    public boolean validIssueToken(String token) {
        return StringUtils.isNotBlank(token) && JIRAConfiguration.ISSUE_PATTERN.matcher(token).matches();
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        return extractJIRAIssuesFromMessage((JIRAConfiguration) issueServiceConfiguration, message);
    }

    @Override
    public Optional<MessageAnnotator> getMessageAnnotator(IssueServiceConfiguration issueServiceConfiguration) {
        JIRAConfiguration configuration = (JIRAConfiguration) issueServiceConfiguration;
        return Optional.of(
                new RegexMessageAnnotator(
                        JIRAConfiguration.ISSUE_PATTERN,
                        key -> MessageAnnotation.of("a")
                                .attr("href", configuration.getIssueURL(key))
                                .text(key)
                )
        );
    }

    @Override
    public String getLinkForAllIssues(IssueServiceConfiguration issueServiceConfiguration, List<Issue> issues) {
        Validate.notNull(issueServiceConfiguration, "The issue service configuration is required");
        Validate.notNull(issues, "The list of issues must not be null");
        JIRAConfiguration configuration = (JIRAConfiguration) issueServiceConfiguration;
        if (issues.size() == 0) {
            // Nothing to link to
            return "";
        } else if (issues.size() == 1) {
            // Link to one issue
            return format(
                    "%s/browse/%s",
                    configuration.getUrl(),
                    issues.iterator().next().getKey()
            );
        } else {
            try {
                return format(
                        "%s/secure/IssueNavigator.jspa?reset=true&mode=hide&jqlQuery=%s",
                        configuration.getUrl(),
                        URLEncoder.encode(
                                format(
                                        "key in (%s)",
                                        StringUtils.join(
                                                issues.stream()
                                                        .map(i -> String.format("\"%s\"", i.getKey()))
                                                        .collect(Collectors.toList()),
                                                ","
                                        )
                                ),
                                "UTF-8"
                        )
                );
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException("UTF-8 not supported");
            }
        }
    }

    @Override
    public Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey) {
        return getIssue((JIRAConfiguration) issueServiceConfiguration, issueKey);
    }

    @Override
    protected Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        JIRAIssue jiraIssue = (JIRAIssue) issue;
        return Collections.singleton(jiraIssue.getIssueType());
    }

    public JIRAIssue getIssue(JIRAConfiguration configuration, String key) {
        try (Transaction tx = transactionService.start()) {
            JIRASession session = getJIRASession(tx, configuration);
            // Gets the JIRA issue
            return session.getClient().getIssue(key, configuration);
        }
    }

    private JIRASession getJIRASession(Transaction tx, final JIRAConfiguration configuration) {
        return tx.getResource(JIRASession.class, configuration.getName(), () -> jiraSessionFactory.create(configuration));
    }

    protected Set<String> extractJIRAIssuesFromMessage(JIRAConfiguration configuration, String message) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = JIRAConfiguration.ISSUE_PATTERN.matcher(message);
            while (matcher.find()) {
                // Gets the issue
                String issueKey = matcher.group();
                // Adds to the result
                if (configuration.isIssue(issueKey)) {
                    result.add(issueKey);
                }
            }
        }
        // OK
        return result;
    }
}
