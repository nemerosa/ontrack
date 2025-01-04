package net.nemerosa.ontrack.extension.support

import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.extension.ExtensionFeature

abstract class AbstractExtension(
    @DocumentationIgnore
    override val feature: ExtensionFeature
) : Extension
