package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Property;
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
        Property<GitHubProjectConfigurationProperty> property = propertyService.getProperty(branch.getProject(), GitHubProjectConfigurationPropertyType.class);
        if (property.isEmpty()) {
            return client -> {
            };
        } else {
            return client -> {
                // TODO OAuth2Token
//                String oAuth2Token = property.getValue().getConfiguration().getOAuth2Token();
//                if (StringUtils.isNotBlank(oAuth2Token)) {
//                    client.setOAuth2Token(oAuth2Token);
//                }
            };
        }
    }
}
