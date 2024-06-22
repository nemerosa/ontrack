package net.nemerosa.ontrack.extension.notifications.sources

import net.nemerosa.ontrack.extension.notifications.model.NotificationSource
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class EntitySubscriptionNotificationSource : NotificationSource<EntitySubscriptionNotificationSourceDataType> {

    override val id: String = "entity-subscription"
    override val displayName: String = "Subscription to an entity"
    override val dataType: KClass<EntitySubscriptionNotificationSourceDataType> =
        EntitySubscriptionNotificationSourceDataType::class

}