package net.nemerosa.ontrack.extension.notifications.channels

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.parseInto
import kotlin.reflect.KClass

abstract class AbstractNotificationChannel<C : Any, R>(
    private val configClass: KClass<C>,
) : NotificationChannel<C, R> {

    override fun validate(channelConfig: JsonNode): ValidatedNotificationChannelConfig<C> = try {
        val config = channelConfig.parseInto(configClass)
        ValidatedNotificationChannelConfig.config(config)
    } catch (ex: Exception) {
        ValidatedNotificationChannelConfig.error(ex)
    }

}