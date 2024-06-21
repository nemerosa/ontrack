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
import net.nemerosa.ontrack.model.tx.TransactionHelper
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
    private val transactionHelper: TransactionHelper,
) : NotificationProcessingService {

    override fun process(item: Notification, context: Map<String, Any>): NotificationResult<*>? {
        meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_started, item)
        val channel = notificationChannelRegistry.findChannel(item.channel)
        if (channel != null) {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_started, item)
            return process(channel, item, context)
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_unknown, item)
            return null
        }
    }

    private fun <C, R> process(
        channel: NotificationChannel<C, R>,
        item: Notification,
        context: Map<String, Any>
    ): NotificationResult<R>? {

        // Unique ID for the record
        val recordId = UUID.randomUUID().toString()

        val validatedConfig = channel.validate(item.channelConfig)
        return if (validatedConfig.config != null) {
            // Current output progress
            var output: R? = null
            val outputProgressCallback: (R) -> R = { current ->
                output = current
                // Saving the current state of the record
                recordResult(
                    recordId = recordId,
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    result = NotificationResult.ongoing(output),
                )
                // OK, returning the new value
                current
            }

            try {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_publishing, item)

                val result = channel.publish(
                    config = validatedConfig.config,
                    event = item.event,
                    context = context,
                    template = item.template,
                    outputProgressCallback = outputProgressCallback,
                )
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_result, item, result)
                recordResult(
                    recordId = recordId,
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    result = result,
                )
                result
            } catch (any: Exception) {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_error, item)
                recordError(
                    recordId = recordId,
                    channel = channel.type,
                    channelConfig = validatedConfig.config,
                    event = item.event,
                    error = any,
                    output = output, // Using the current output, even for errors
                )
                NotificationResult.error(any.message ?: any::class.java.name, output)
            }
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_invalid, item)
            recordInvalidConfig(
                recordId = recordId,
                channel = channel.type,
                invalidChannelConfig = item.channelConfig,
                event = item.event,
            )
            null
        }
    }

    private fun record(
        record: NotificationRecord,
    ) {
        transactionHelper.inNewTransaction {
            notificationRecordingService.record(record)
        }
    }

    private fun recordResult(
        recordId: String,
        channel: String,
        channelConfig: Any,
        event: Event,
        result: NotificationResult<*>,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                timestamp = Time.now(),
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = result.toNotificationRecordResult(),
            )
        )
    }

    private fun recordError(
        recordId: String,
        channel: String,
        channelConfig: Any,
        event: Event,
        error: Exception,
        output: Any?,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                timestamp = Time.now(),
                channel = channel,
                channelConfig = channelConfig.asJson(),
                event = event.asJson(),
                result = NotificationResult.error<Any>(ExceptionUtils.getStackTrace(error), output)
                    .toNotificationRecordResult(),
            )
        )
    }

    private fun recordInvalidConfig(
        recordId: String,
        channel: String,
        invalidChannelConfig: JsonNode,
        event: Event,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                timestamp = Time.now(),
                channel = channel,
                channelConfig = invalidChannelConfig,
                event = event.asJson(),
                result = NotificationResult.invalidConfiguration<Any>().toNotificationRecordResult(),
            )
        )
    }

}