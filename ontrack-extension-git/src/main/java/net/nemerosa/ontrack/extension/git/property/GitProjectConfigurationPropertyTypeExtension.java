package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitProjectConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<GitBranchConfigurationProperty> {

    @Autowired
    public GitProjectConfigurationPropertyTypeExtension(GitExtensionFeature extensionFeature) {
        super(extensionFeature, new GitBranchConfigurationPropertyType());
    }
}
