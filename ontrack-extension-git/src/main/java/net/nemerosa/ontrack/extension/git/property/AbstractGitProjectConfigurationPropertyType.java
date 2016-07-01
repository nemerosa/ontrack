package net.nemerosa.ontrack.extension.git.property;

import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;

public abstract class AbstractGitProjectConfigurationPropertyType<T> extends AbstractPropertyType<T> {

    protected AbstractGitProjectConfigurationPropertyType(ExtensionFeature extensionFeature) {
        super(extensionFeature);
    }

}
