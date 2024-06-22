package net.nemerosa.ontrack.extension.notifications.sources

import net.nemerosa.ontrack.extension.notifications.model.NotificationSource
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GlobalSubscriptionNotificationSource : NotificationSource<GlobalSubscriptionNotificationSourceDataType> {

    override val id: String = "global-subscription"
    override val displayName: String = "Global subscription"
    override val dataType: KClass<GlobalSubscriptionNotificationSourceDataType> =
        GlobalSubscriptionNotificationSourceDataType::class

}