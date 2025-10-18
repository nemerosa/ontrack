package net.nemerosa.ontrack.extension.notifications.ci

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.extensions.CIConfigExtension
import net.nemerosa.ontrack.extension.notifications.NotificationsExtensionFeature
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscription
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionOrigins
import net.nemerosa.ontrack.extension.notifications.subscriptions.EventSubscriptionService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class NotificationsCIConfigExtension(
    notificationsExtensionFeature: NotificationsExtensionFeature,
    private val eventSubscriptionService: EventSubscriptionService,
    private val structureService: StructureService,
) : AbstractExtension(notificationsExtensionFeature), CIConfigExtension<NotificationsCIConfig> {

    override val id: String = "notificationsConfig"

    override val projectEntityTypes: Set<ProjectEntityType> = setOf(ProjectEntityType.BRANCH)

    override fun parseData(data: JsonNode): NotificationsCIConfig = data.parse()

    override fun configure(
        entity: ProjectEntity,
        data: NotificationsCIConfig
    ) {
        data.notifications.forEach { notification ->
            configureNotification(
                branch = entity as Branch,
                notification = notification,
            )
        }
    }

    private fun configureNotification(
        branch: Branch,
        notification: NotificationsCIConfigItem
    ) {
        // Target for the subscription
        val target = when {
            !notification.promotion.isNullOrBlank() -> getPromotion(branch, notification.promotion)
            else -> branch
        }

        val events = notification.events.toSet()
            .takeIf { it.isNotEmpty() }
            ?: throw NotificationsCIConfigException("Events are required")

        // Subscription
        eventSubscriptionService.subscribe(
            EventSubscription(
                projectEntity = target,
                name = notification.name,
                events = events,
                keywords = notification.keywords,
                channel = notification.channel ?: throw NotificationsCIConfigException("Channel is required"),
                channelConfig = notification.channelConfig,
                disabled = false,
                origin = EventSubscriptionOrigins.CI,
                contentTemplate = notification.contentTemplate,
            )
        )
    }

    private fun getPromotion(
        branch: Branch,
        promotion: String
    ): ProjectEntity =
        structureService.findPromotionLevelByName(
            branch.project.name,
            branch.name,
            promotion
        ).getOrNull()
            ?: throw NotificationsCIConfigPromotionNotFoundException(promotion)
}