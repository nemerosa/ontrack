package net.nemerosa.ontrack.extension.notifications.processing

import com.fasterxml.jackson.databind.JsonNode
import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.metrics.NotificationsMetrics
import net.nemerosa.ontrack.extension.notifications.metrics.incrementForProcessing
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord
import net.nemerosa.ontrack.extension.notifications.recording.NotificationRecordingService
import net.nemerosa.ontrack.extension.notifications.recording.toNotificationRecordResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class DefaultNotificationProcessingService(
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val notificationRecordingService: NotificationRecordingService,
    private val meterRegistry: MeterRegistry,
) : NotificationProcessingService {

    override fun process(item: Notification) {
        meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_started, item)
        val channel = notificationChannelRegistry.findChannel(item.channel)
        if (channel != null) {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_started, item)
            process(channel, item)
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_unknown, item)
        }
    }

    private fun <C, R> process(channel: NotificationChannel<C, R>, item: Notification) {
        val validatedConfig = channel.validate(item.channelConfig)
        if (validatedConfig.config != null) {

            // Current output progress
            var output: R? = null
            val outputProgressCallback: (R) -> R = { current ->
                output = current
                current
            }

            try {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_publishing, item)

                val result = channel.publish(
                    config = validatedConfig.config,
                    event = item.event,
                    template = item.template,
                    outputProgressCallback = outputProgressCallback,
                )
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_result, item, result)
                record(
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    result = result,
                )
            } catch (any: Exception) {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_error, item)
                record(
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    error = any,
                    output = output, // Using the current output, even for errors
                )
            }
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_invalid, item)
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
        result: NotificationResult<*>,
    ) {
        notificationRecordingService.record(
            NotificationRecord(
                id = UUID.randomUUID().toString(),
                timestamp = Time.now(),
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = result.toNotificationRecordResult(),
            )
        )
    }

    private fun record(
        channel: String,
        channelConfig: Any,
        event: Event,
        error: Exception,
        output: Any?,
    ) {
        notificationRecordingService.record(
            NotificationRecord(
                id = UUID.randomUUID().toString(),
                timestamp = Time.now(),
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = NotificationResult.error<Any>(ExceptionUtils.getStackTrace(error), output)
                    .toNotificationRecordResult(),
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
                id = UUID.randomUUID().toString(),
                timestamp = Time.now(),
                channel = channel,
                channelConfig = invalidChannelConfig,
                event = event.asJson(),
                result = NotificationResult.invalidConfiguration<Any>().toNotificationRecordResult(),
            )
        )
    }

}