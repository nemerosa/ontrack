package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.extension.ExtensionFeature

abstract class AbstractGitProjectConfigurationPropertyType<T>
protected constructor(extensionFeature: ExtensionFeature) :
    AbstractPropertyType<T>(extensionFeature)
