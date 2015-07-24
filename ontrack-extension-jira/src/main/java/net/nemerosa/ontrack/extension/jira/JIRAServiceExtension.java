package net.nemerosa.ontrack.extension.jira;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
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
    private final PropertyService propertyService;

    @Autowired
    public JIRAServiceExtension(
            JIRAExtensionFeature extensionFeature,
            JIRAConfigurationService jiraConfigurationService,
            JIRASessionFactory jiraSessionFactory,
            TransactionService transactionService,
            IssueExportServiceFactory issueExportServiceFactory,
            PropertyService propertyService) {
        super(extensionFeature, SERVICE, "JIRA", issueExportServiceFactory);
        this.jiraConfigurationService = jiraConfigurationService;
        this.jiraSessionFactory = jiraSessionFactory;
        this.transactionService = transactionService;
        this.propertyService = propertyService;
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
    public Collection<? extends Issue> getLinkedIssues(Project project, IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        // Gets a list of link names to follow
        return propertyService.getProperty(project, JIRAFollowLinksPropertyType.class).option()
                .map(property -> {
                    Map<String, JIRAIssue> issues = new LinkedHashMap<>();
                    followLinks(
                            (JIRAConfiguration) issueServiceConfiguration,
                            (JIRAIssue) issue,
                            Sets.newHashSet(property.getLinkNames()),
                            issues
                    );
                    return issues.values();
                })
                .orElse(Collections.singleton((JIRAIssue) issue));
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
        if (issue != null) {
            JIRAIssue jiraIssue = (JIRAIssue) issue;
            return Collections.singleton(jiraIssue.getIssueType());
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Given an issue seed, and a list of link names, follows the given links recursively and
     * puts the associated issues into the {@code collectedIssues} map.
     *
     * @param configuration   JIRA configuration to use to load the issues
     * @param seed            Issue to start from.
     * @param linkNames       Links to follow
     * @param collectedIssues Collected issues, indexed by their key
     */
    public void followLinks(JIRAConfiguration configuration, JIRAIssue seed, Set<String> linkNames, Map<String, JIRAIssue> collectedIssues) {
        try (Transaction tx = transactionService.start()) {
            JIRASession session = getJIRASession(tx, configuration);
            // Gets the client from the current session
            JIRAClient client = session.getClient();
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
