package net.nemerosa.ontrack.extension.github;

import net.nemerosa.ontrack.extension.github.client.GitHubClientConfiguratorFactory;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import net.nemerosa.ontrack.model.support.MessageAnnotation;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.model.support.RegexMessageAnnotator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitHubIssueServiceExtension extends AbstractIssueServiceExtension {

    public static final String GITHUB_SERVICE_ID = "github";
    public static final String GITHUB_ISSUE_PATTERN = "#\\d+";
    private final GitHubConfigurationService configurationService;
    private final GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory;
    private final OntrackGitHubClient gitHubClient;

    @Autowired
    public GitHubIssueServiceExtension(
            GitHubExtensionFeature extensionFeature,
            GitHubConfigurationService configurationService,
            GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory,
            OntrackGitHubClient gitHubClient) {
        super(extensionFeature, GITHUB_SERVICE_ID, "GitHub");
        this.configurationService = configurationService;
        this.gitHubClientConfiguratorFactory = gitHubClientConfiguratorFactory;
        this.gitHubClient = gitHubClient;
    }

    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        return configurationService.getConfigurations();
    }

    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        return configurationService.getConfiguration(name);
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
                String issueKey = matcher.group();
                // Removes the trailing #
                issueKey = issueKey.substring(1);
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
                        key -> {
                            String id = key.substring(1);
                            return MessageAnnotation.of("a")
                                    .attr(
                                            "href",
                                            String.format(
                                                    "https://github.com/%s/issues/%s",
                                                    configuration.getRepository(),
                                                    id
                                            )
                                    )
                                    .text(key);
                        }
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
                Integer.parseInt(issueKey, 10)
        );
    }
}
