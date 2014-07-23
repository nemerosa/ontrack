package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.git.service.GitConfigurationService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitBranchConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<GitProjectConfigurationProperty> {

    @Autowired
    public GitBranchConfigurationPropertyTypeExtension(GitExtensionFeature extensionFeature, GitConfigurationService configurationService) {
        super(extensionFeature, new GitProjectConfigurationPropertyType(configurationService));
    }
}
