package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubClientConfiguratorFactory implements GitHubClientConfiguratorFactory {

    private final PropertyService propertyService;

    @Autowired
    public DefaultGitHubClientConfiguratorFactory(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @Override
    public GitHubClientConfigurator getGitHubConfigurator(Branch branch) {
        // TODO GitHubProjectProperty
        return null;
        // Gets the authentication parameters
//        final String authentication = propertiesService.getPropertyValue(Entity.PROJECT, projectId, GitHubExtension.EXTENSION, GitHubAuthenticationProperty.NAME);
        // Configurator
//        return new GitHubClientConfigurator() {
//            @Override
//            public void configure(GitHubClient client) {
//                if (StringUtils.isNotBlank(authentication)) {
//                    client.setOAuth2Token(authentication);
//                }
//            }
//        };
    }
}
