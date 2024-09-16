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

    override fun process(
        item: Notification,
        context: Map<String, Any>,
        outputFeedback: (output: Any?) -> Unit,
    ): NotificationResult<*>? {
        meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_started, item)
        val channel = notificationChannelRegistry.findChannel(item.channel)
        if (channel != null) {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_started, item)
            return process(channel, item, context, outputFeedback)
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_unknown, item)
            return null
        }
    }

    private fun <C, R> process(
        channel: NotificationChannel<C, R>,
        item: Notification,
        context: Map<String, Any>,
        outputFeedback: (output: R) -> Unit,
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
                    item = item,
                    validatedConfig = validatedConfig.config.asJson(),
                    result = NotificationResult.ongoing(output),
                )
                // Feedback
                outputFeedback(current)
                // OK, returning the new value
                current
            }

            try {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_publishing, item)

                val result = channel.publish(
                    recordId = recordId,
                    config = validatedConfig.config,
                    event = item.event,
                    context = context,
                    template = item.template,
                    outputProgressCallback = outputProgressCallback,
                )
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_result, item, result)
                recordResult(
                    recordId = recordId,
                    item = item,
                    validatedConfig = validatedConfig.config.asJson(),
                    result = result,
                )
                result
            } catch (any: Throwable) {
                meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_error, item)
                recordError(
                    recordId = recordId,
                    item = item,
                    validatedConfig = validatedConfig.config.asJson(),
                    error = any,
                    output = output, // Using the current output, even for errors
                )
                NotificationResult.error(any.message ?: any::class.java.name, output)
            }
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_invalid, item)
            recordInvalidConfig(
                recordId = recordId,
                item = item,
                invalidChannelConfig = item.channelConfig,
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
        item: Notification,
        validatedConfig: JsonNode,
        result: NotificationResult<*>,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                source = item.source,
                timestamp = Time.now(),
                channel = item.channel,
                channelConfig = validatedConfig,
                event = item.event.asJson(),
                result = result.toNotificationRecordResult(),
            )
        )
    }

    private fun recordError(
        recordId: String,
        item: Notification,
        validatedConfig: JsonNode,
        error: Throwable,
        output: Any?,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                source = item.source,
                timestamp = Time.now(),
                channel = item.channel,
                channelConfig = validatedConfig,
                event = item.event.asJson(),
                result = NotificationResult.error(ExceptionUtils.getStackTrace(error), output)
                    .toNotificationRecordResult(),
            )
        )
    }

    private fun recordInvalidConfig(
        recordId: String,
        item: Notification,
        invalidChannelConfig: JsonNode,
    ) {
        record(
            NotificationRecord(
                id = recordId,
                source = item.source,
                timestamp = Time.now(),
                channel = item.channel,
                channelConfig = invalidChannelConfig,
                event = item.event.asJson(),
                result = NotificationResult.invalidConfiguration<Any>().toNotificationRecordResult(),
            )
        )
    }

}