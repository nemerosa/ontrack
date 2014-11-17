package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.git.service.BuildGitCommitLinkService;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitBranchConfigurationPropertyTypeExtension extends AbstractPropertyTypeExtension<GitBranchConfigurationProperty> {

    @Autowired
    public GitBranchConfigurationPropertyTypeExtension(GitExtensionFeature extensionFeature, BuildGitCommitLinkService buildGitCommitLinkService) {
        super(extensionFeature, new GitBranchConfigurationPropertyType(buildGitCommitLinkService));
    }
}
