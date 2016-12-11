package net.nemerosa.ontrack.extension.gitlab.service;

import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClient;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
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
public class GitLabConfigurationServiceImpl extends AbstractConfigurationService<GitLabConfiguration> implements GitLabConfigurationService {

    private final OntrackGitLabClientFactory gitLabClientFactory;
    private final ApplicationLogService applicationLogService;

    @Autowired
    public GitLabConfigurationServiceImpl(
            ConfigurationRepository configurationRepository,
            SecurityService securityService,
            EncryptionService encryptionService,
            EventPostService eventPostService,
            EventFactory eventFactory,
            OntrackGitLabClientFactory gitLabClientFactory,
            OntrackConfigProperties ontrackConfigProperties,
            ApplicationLogService applicationLogService) {
        super(
                GitLabConfiguration.class,
                configurationRepository,
                securityService,
                encryptionService,
                eventPostService,
                eventFactory,
                ontrackConfigProperties
        );
        this.gitLabClientFactory = gitLabClientFactory;
        this.applicationLogService = applicationLogService;
    }

    /**
     * No need to inject a password since this is not supported.
     */
    @Override
    protected GitLabConfiguration injectCredentials(GitLabConfiguration configuration) {
        return configuration;
    }

    @Override
    protected ConnectionResult validate(GitLabConfiguration configuration) {
        try {
            // Gets the client
            OntrackGitLabClient client = gitLabClientFactory.create(configuration);
            // Gets the list of repositories
            client.getRepositories();
            // OK
            return ConnectionResult.ok();
        } catch (Exception ex) {
            applicationLogService.log(
                    ApplicationLogEntry.error(
                            ex,
                            NameDescription.nd("gitlab", "GitLab connection issue"),
                            configuration.getUrl()
                    )
                            .withDetail("gitlab-config-name", configuration.getName())
                            .withDetail("gitlab-config-url", configuration.getUrl())
            );
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
