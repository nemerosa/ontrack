package net.nemerosa.ontrack.extension.github.property;

import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitHubProjectConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<GitHubProjectConfigurationProperty> {

    @Autowired
    public GitHubProjectConfigurationPropertyTypeExtension(GitHubExtensionFeature extensionFeature, GitHubConfigurationService configurationService) {
        super(extensionFeature, new GitHubProjectConfigurationPropertyType(configurationService));
    }
}
