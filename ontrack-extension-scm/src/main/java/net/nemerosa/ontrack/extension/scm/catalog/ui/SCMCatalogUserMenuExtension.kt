package net.nemerosa.ontrack.extension.scm.catalog.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogAccessFunction
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class SCMCatalogUserMenuExtension(
        extensionFeature: SCMExtensionFeature
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override val action: Action = Action.of("scm-catalog", "SCM Catalog", "catalog")
        .withGroup(UserMenuExtensionGroups.information)

    override val globalFunction: Class<out GlobalFunction> = SCMCatalogAccessFunction::class.java
}