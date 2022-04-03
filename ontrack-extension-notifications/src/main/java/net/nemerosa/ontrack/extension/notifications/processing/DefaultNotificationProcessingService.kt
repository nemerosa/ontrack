package net.nemerosa.ontrack.extension.notifications.processing

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultNotificationProcessingService(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val notificationRecordingService: NotificationRecordingService,
) : NotificationProcessingService {

    // TODO Notifications processing metrics

    override fun process(item: Notification) {
        val channel = notificationChannelRegistry.findChannel(item.channel)
        if (channel != null) {
            process(channel, item)
        }
    }

    private fun <C> process(channel: NotificationChannel<C>, item: Notification) {
        val validatedConfig = channel.validate(item.channelConfig)
        if (validatedConfig.config != null) {
            try {
                val result = channel.publish(validatedConfig.config, item.event)
                record(
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    result = result,
                )
            } catch (any: Exception) {
                record(
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    error = any,
                )
            }
        } else {
            record(
                channel = channel.type,
                invalidChannelConfig = item.channelConfig,
                event = item.event,
            )
        }
    }

    private fun record(
        channel: String,
        channelConfig: Any,
        event: Event,
        result: NotificationResult,
    ) {
        notificationRecordingService.record(
            NotificationRecord(
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = result,
            )
        )
    }

    private fun record(
        channel: String,
        channelConfig: Any,
        event: Event,
        error: Exception,
    ) {
        notificationRecordingService.record(
            NotificationRecord(
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = NotificationResult.error(ExceptionUtils.getStackTrace(error)),
            )
        )
    }

    private fun record(
        channel: String,
        invalidChannelConfig: JsonNode,
        event: Event,
    ) {
        notificationRecordingService.record(
            NotificationRecord(
                channel = channel,
                channelConfig = invalidChannelConfig,
                event = event.asJson(),
                result = NotificationResult.invalidConfiguration(),
            )
        )
    }

}