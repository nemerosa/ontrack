package net.nemerosa.ontrack.extension.notifications.mock

import net.nemerosa.ontrack.extension.notifications.model.NotificationSource
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class MockNotificationSource : NotificationSource<MockNotificationSourceDataType> {

    override val id: String = "mock"
    override val displayName: String = "Mock notification source"
    override val dataType: KClass<MockNotificationSourceDataType> =
        MockNotificationSourceDataType::class
    
}