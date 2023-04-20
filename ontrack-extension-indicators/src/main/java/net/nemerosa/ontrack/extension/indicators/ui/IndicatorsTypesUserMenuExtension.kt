package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.acl.IndicatorTypeManagement
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class IndicatorsTypesUserMenuExtension(
    extension: IndicatorsExtensionFeature,
) : AbstractExtension(extension), UserMenuExtension {

    override val action: Action =
        Action.of("indicators-types", "Types", "types")
            .withGroup(IndicatorsUserMenuExtensionGroups.indicators)

    override val globalFunction: Class<out GlobalFunction> = IndicatorTypeManagement::class.java

}