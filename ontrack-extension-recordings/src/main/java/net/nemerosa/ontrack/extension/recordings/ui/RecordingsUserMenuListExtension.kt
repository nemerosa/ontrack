package net.nemerosa.ontrack.extension.recordings.ui

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.UserMenuExtension
import net.nemerosa.ontrack.extension.api.UserMenuExtensionGroups
import net.nemerosa.ontrack.extension.api.UserMenuListExtension
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.recordings.RecordingsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class RecordingsUserMenuListExtension(
        private val extensionFeature: RecordingsExtensionFeature,
        private val extensionManager: ExtensionManager,
) : AbstractExtension(extensionFeature), UserMenuListExtension {

    override val userMenuExtensions: List<UserMenuExtension>
        get() = extensionManager.getExtensions(RecordingsExtension::class.java).map { extension ->
            RecordingsUserMenuExtension(extension)
        }

    private inner class RecordingsUserMenuExtension(
            extension: RecordingsExtension<*, *>,
    ) : AbstractExtension(extensionFeature), UserMenuExtension {

        override val globalFunction: Class<out GlobalFunction> = ApplicationManagement::class.java
        override val action: Action = Action.of(
                id = "recordings-${extension.id}",
                name = "${extension.displayName} recordings",
                uri = "extension/${extension.feature.id}/recordings",
        ).withGroup(UserMenuExtensionGroups.information)

    }
}