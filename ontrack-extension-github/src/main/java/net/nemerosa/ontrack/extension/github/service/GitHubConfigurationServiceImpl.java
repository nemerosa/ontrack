package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory;
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GitHubConfigurationServiceImpl extends AbstractConfigurationService<GitHubEngineConfiguration> implements GitHubConfigurationService {

    private final OntrackGitHubClientFactory gitHubClientFactory;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public GitHubConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory, OntrackGitHubClientFactory gitHubClientFactory, OntrackConfigProperties ontrackConfigProperties, ApplicationLogService applicationLogService) {
        super(GitHubEngineConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
        this.gitHubClientFactory = gitHubClientFactory;
        this.applicationLogService = applicationLogService;
    }

    @Override
    protected ConnectionResult validate(GitHubEngineConfiguration configuration) {
        try {
            // Gets the client
            OntrackGitHubClient client = gitHubClientFactory.create(configuration);
            // Gets the list of repositories
            client.getRepositories();
            // OK
            return ConnectionResult.ok();
        } catch (Exception ex) {
            applicationLogService.log(
                    ApplicationLogEntry.error(
                            ex,
                            NameDescription.nd("github", "GitHub connection issue"),
                            configuration.getUrl()
                    )
                            .withDetail("github-config-name", configuration.getName())
                            .withDetail("github-config-url", configuration.getUrl())
            );
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
