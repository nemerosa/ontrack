package net.nemerosa.ontrack.extension.hook.ui

import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.hook.HookExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class HookRecordsUserMenuExtension(
        extensionFeature: HookExtensionFeature,
) : AbstractExtension(extensionFeature), UserMenuExtension {

    override val globalFunction: Class<out GlobalFunction> = GlobalSettings::class.java

    override val action: Action = Action.of(
            id = "hook-records",
            name = "Hook records",
            uri = "records",
    ).withGroup(UserMenuExtensionGroups.information)

}