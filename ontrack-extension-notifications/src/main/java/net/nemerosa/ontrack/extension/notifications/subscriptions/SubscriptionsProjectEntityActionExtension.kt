package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component
import java.util.*

@Component
class SubscriptionsProjectEntityActionExtension(
    extensionFeature: NotificationsExtensionFeature,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> =
        if (securityService.isProjectFunctionGranted(entity, ProjectSubscriptionsRead::class.java)) {
            Optional.of(
                Action.of(
                    "entity-subscriptions",
                    "Subscriptions",
                    "entity-subscriptions/${entity.projectEntityType}/${entity.id()}"
                )
            )
        } else {
            Optional.empty()
        }
}
