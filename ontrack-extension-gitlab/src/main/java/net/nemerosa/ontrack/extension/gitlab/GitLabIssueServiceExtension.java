package net.nemerosa.ontrack.extension.gitlab;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClient;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper;
import net.nemerosa.ontrack.extension.gitlab.property.GitLabGitConfiguration;
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.model.support.MessageAnnotation;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitLabIssueServiceExtension extends AbstractIssueServiceExtension {

    public static final String GITLAB_SERVICE_ID = "gitlab";
    public static final String GITLAB_ISSUE_PATTERN = "#(\\d+)";
    private final GitLabConfigurationService configurationService;
    private final OntrackGitLabClientFactory gitLabClientFactory;

    /**
     * Constructor.
     */
    protected GitLabIssueServiceExtension(GitLabExtensionFeature extensionFeature, IssueExportServiceFactory issueExportServiceFactory, GitLabConfigurationService configurationService, OntrackGitLabClientFactory gitLabClientFactory) {
        super(extensionFeature, GITLAB_SERVICE_ID, "GitLab", issueExportServiceFactory);
        this.configurationService = configurationService;
        this.gitLabClientFactory = gitLabClientFactory;
    }

    /**
     * The GitLab configurations are not selectable outside GitLab configurations and this method returns an empty list.
     */
    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        return Collections.emptyList();
    }

    /**
     * A GitLab configuration name
     *
     * @param name Name of the configuration and repository.
     * @return Wrapper for the GitHub issue service.
     * @see net.nemerosa.ontrack.extension.gitlab.property.GitLabGitConfiguration
     */
    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        // Parsing of the name
        String[] tokens = StringUtils.split(name, GitLabGitConfiguration.CONFIGURATION_REPOSITORY_SEPARATOR);
        if (tokens == null || tokens.length != 2) {
            throw new IllegalStateException("The GitLab issue configuration identifier name is expected using configuration:repository as a format");
        }
        String configuration = tokens[0];
        String repository = tokens[1];
        return new GitLabIssueServiceConfiguration(
                configurationService.getConfiguration(configuration),
                repository
        );
    }

    @Override
    public boolean validIssueToken(String token) {
        return Pattern.matches(GITLAB_ISSUE_PATTERN, token);
    }

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = Pattern.compile(GITLAB_ISSUE_PATTERN).matcher(message);
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
        GitLabIssueServiceConfiguration configuration = (GitLabIssueServiceConfiguration) issueServiceConfiguration;
        return Optional.of(
                new RegexMessageAnnotator(
                        GITLAB_ISSUE_PATTERN,
                        key -> MessageAnnotation.of("a")
                                .attr(
                                        "href",
                                        String.format(
                                                "%s/%s/issues/%s",
                                                configuration.getConfiguration().getUrl(),
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
        GitLabIssueServiceConfiguration configuration = (GitLabIssueServiceConfiguration) issueServiceConfiguration;
        OntrackGitLabClient client = gitLabClientFactory.create(configuration.getConfiguration());
        return client.getIssue(
                configuration.getRepository(),
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
    protected Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue) {
        GitLabIssueWrapper wrapper = (GitLabIssueWrapper) issue;
        return Sets.newLinkedHashSet(
                wrapper.getLabels()
        );
    }
}
