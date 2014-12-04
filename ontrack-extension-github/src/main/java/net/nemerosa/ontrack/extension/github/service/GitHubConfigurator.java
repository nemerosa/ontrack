package net.nemerosa.ontrack.extension.github.service;

import net.nemerosa.ontrack.extension.git.model.FormerGitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitConfigurator;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubConfigurator implements GitConfigurator {

    private final PropertyService propertyService;

    @Autowired
    public GitHubConfigurator(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public FormerGitConfiguration configure(FormerGitConfiguration configuration, Branch branch) {
        return configureProject(configuration, branch.getProject());
    }

    @Override
    public FormerGitConfiguration configureProject(FormerGitConfiguration configuration, Project project) {
        // Project GitHub configuration
        Property<GitHubProjectConfigurationProperty> projectConfig = propertyService.getProperty(project, GitHubProjectConfigurationPropertyType.class);
        if (!projectConfig.isEmpty()) {
            // GitHub configuration
            GitHubConfiguration gitHub = projectConfig.getValue().getConfiguration();
            // Merge the project configuration
            FormerGitConfiguration gitHubConfig = configuration
                    .withRemote(gitHub.getRemote())
                    .withIndexationInterval(gitHub.getIndexationInterval())
                    .withCommitLink(gitHub.getCommitLink())
                    .withFileAtCommitLink(gitHub.getFileAtCommitLink())
                    .withIssueServiceConfigurationIdentifier(gitHub.toIdentifier().format());
            // User / password
            String oAuth2Token = gitHub.getOauth2Token();
            String user = gitHub.getUser();
            if (StringUtils.isNotBlank(oAuth2Token)) {
                gitHubConfig = gitHubConfig
                        .withUser(oAuth2Token)
                        .withPassword("x-oauth-basic");
            } else if (StringUtils.isNotBlank(user)) {
                gitHubConfig = gitHubConfig
                        .withUser(gitHub.getUser())
                        .withPassword(gitHub.getPassword());
            }
            // OK
            return gitHubConfig;
        } else {
            return configuration;
        }
    }

}
