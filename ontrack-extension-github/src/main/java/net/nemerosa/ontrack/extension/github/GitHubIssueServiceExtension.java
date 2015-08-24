package net.nemerosa.ontrack.extension.github;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.github.client.GitHubClientConfiguratorFactory;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.model.GitHubIssue;
import net.nemerosa.ontrack.extension.github.model.GitHubLabel;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.github.service.GitHubIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.model.support.MessageAnnotation;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GitHubIssueServiceExtension extends AbstractIssueServiceExtension {

    public static final String GITHUB_SERVICE_ID = "github";
    public static final String GITHUB_ISSUE_PATTERN = "#(\\d+)";
    private final GitHubConfigurationService configurationService;
    private final GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory;
    private final OntrackGitHubClient gitHubClient;

    @Autowired
    public GitHubIssueServiceExtension(
            GitHubExtensionFeature extensionFeature,
            GitHubConfigurationService configurationService,
            GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory,
            OntrackGitHubClient gitHubClient,
            IssueExportServiceFactory issueExportServiceFactory
    ) {
        super(extensionFeature, GITHUB_SERVICE_ID, "GitHub", issueExportServiceFactory);
        this.configurationService = configurationService;
        this.gitHubClientConfiguratorFactory = gitHubClientConfiguratorFactory;
        this.gitHubClient = gitHubClient;
    }

    /**
     * The GitHub configurations are not selectable and this method returns an empty list.
     */
    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        return Collections.emptyList();
    }

    /**
     * A GitHub configuration name
     *
     * @param name Name of the configuration
     * @return Wrapper for the GitHub issue service.
     */
    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        return new GitHubIssueServiceConfiguration(
                configurationService.getConfiguration(name)
        );
    }

    @Override
    public boolean validIssueToken(String token) {
        return Pattern.matches(GITHUB_ISSUE_PATTERN, token);
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = Pattern.compile(GITHUB_ISSUE_PATTERN).matcher(message);
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
    public Optional<MessageAnnotator> getMessageAnnotator(IssueServiceConfiguration issueServiceConfiguration) {
        GitHubConfiguration configuration = (GitHubConfiguration) issueServiceConfiguration;
        return Optional.of(
                new RegexMessageAnnotator(
                        GITHUB_ISSUE_PATTERN,
                        key -> MessageAnnotation.of("a")
                                .attr(
                                        "href",
                                        String.format(
                                                "https://github.com/%s/issues/%s",
                                                configuration.getRepository(),
                                                key.substring(1)
                                        )
                                )
                                .text(key)
                )
        );
    }

    @Override
    public String getLinkForAllIssues(IssueServiceConfiguration issueServiceConfiguration, List<Issue> issues) {
        return null;
    }

    @Override
    public Issue getIssue(IssueServiceConfiguration issueServiceConfiguration, String issueKey) {
        GitHubConfiguration configuration = (GitHubConfiguration) issueServiceConfiguration;
        return gitHubClient.getIssue(
                configuration.getRepository(),
                gitHubClientConfiguratorFactory.getGitHubConfigurator(configuration),
                getIssueId(issueKey)
        );
    }

    @Override
    public Optional<String> getIssueId(IssueServiceConfiguration issueServiceConfiguration, String token) {
        if (StringUtils.isNumeric(token) || validIssueToken(token)) {
            return Optional.of(String.valueOf(getIssueId(token)));
        } else {
            return Optional.empty();
        }
    }

    protected int getIssueId(String token) {
        return Integer.parseInt(StringUtils.stripStart(token, "#"), 10);
    }

    @Override
    public boolean containsIssueKey(IssueServiceConfiguration issueServiceConfiguration, String key, Set<String> keys) {
        // Searchable key?
        if (StringUtils.isNumeric(key) || validIssueToken(key)) {
            Set<Integer> ids = keys.stream().map(this::getIssueId).collect(Collectors.toSet());
            return ids.contains(getIssueId(key));
        } else {
            return false;
        }
    }

    @Override
    protected Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        GitHubIssue gitHubIssue = (GitHubIssue) issue;
        return Sets.newLinkedHashSet(
                gitHubIssue.getLabels().stream().map(GitHubLabel::getName).collect(Collectors.toList())
        );
    }
}
