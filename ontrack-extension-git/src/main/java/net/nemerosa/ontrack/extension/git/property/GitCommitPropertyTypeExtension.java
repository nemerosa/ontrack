package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.support.AbstractPropertyTypeExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitCommitPropertyTypeExtension extends AbstractPropertyTypeExtension<GitCommitProperty> {

    @Autowired
    public GitCommitPropertyTypeExtension(GitExtensionFeature extensionFeature) {
        super(extensionFeature, new GitCommitPropertyType());
    }
}
