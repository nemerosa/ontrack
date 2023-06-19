package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsConfigProperties
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.Action
import net.nemerosa.ontrack.model.support.ActionType
import org.springframework.stereotype.Component
import java.util.*

@Component
class SubscriptionsProjectEntityActionExtension(
    extensionFeature: NotificationsExtensionFeature,
    private val securityService: SecurityService,
    private val notificationsConfigProperties: NotificationsConfigProperties,
) : AbstractExtension(extensionFeature), ProjectEntityActionExtension {

    override fun getAction(entity: ProjectEntity): Optional<Action> =
        if (securityService.isProjectFunctionGranted(entity, ProjectSubscriptionsRead::class.java)) {
            Optional.of(
                Action(
                    id = "entity-subscriptions",
                    name = "Subscriptions",
                    uri = "entity-subscriptions/${entity.projectEntityType}/${entity.id()}",
                    type = ActionType.LINK,
                    enabled = notificationsConfigProperties.enabled,
                )
            )
        } else {
            Optional.empty()
        }
}
