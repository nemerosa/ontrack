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
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.tx.TransactionHelper
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val securityService: SecurityService,
) : NotificationProcessingService {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun process(
        item: Notification,
        context: Map<String, Any>,
        outputFeedback: (recordId: String, output: Any?) -> Unit,
    ): NotificationProcessingResult<*>? {
        logger.debug(
            "Processing notification (user={}) {}",
            securityService.currentUser?.name,
            item
        )
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
        outputFeedback: (recordId: String, output: R) -> Unit,
    ): NotificationProcessingResult<R> {

        // Unique ID for the record
        val recordId = UUID.randomUUID().toString()

        val validatedConfig = channel.validate(item.channelConfig)
        return if (validatedConfig.config != null) {
            val channelResult = channelProcess(
                recordId = recordId,
                item = item,
                config = validatedConfig.config,
                outputFeedback = outputFeedback,
                channel = channel,
                context = context
            )
            NotificationProcessingResult(
                recordId = recordId,
                result = channelResult,
            )
        } else {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_invalid, item)
            recordInvalidConfig(
                recordId = recordId,
                item = item,
                invalidChannelConfig = item.channelConfig,
            )
            NotificationProcessingResult(
                recordId = recordId,
                result = null,
            )
        }
    }

    private fun <C, R> channelProcess(
        recordId: String,
        item: Notification,
        config: C,
        outputFeedback: (recordId: String, output: R) -> Unit,
        channel: NotificationChannel<C, R>,
        context: Map<String, Any>
    ): NotificationResult<R> {

        // Current output progress
        var output: R? = null
        val outputProgressCallback: (R) -> R = { current ->
            output = current
            // Saving the current state of the record
            recordResult(
                recordId = recordId,
                item = item,
                validatedConfig = config.asJson(),
                result = NotificationResult.ongoing(output),
            )
            // Feedback
            outputFeedback(recordId, current)
            // OK, returning the new value
            current
        }

        return try {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_publishing, item)

            val result = channel.publish(
                recordId = recordId,
                config = config,
                event = item.event,
                context = context,
                template = item.template,
                outputProgressCallback = outputProgressCallback,
            )
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_result, item, result)
            recordResult(
                recordId = recordId,
                item = item,
                validatedConfig = config.asJson(),
                result = result,
            )
            result
        } catch (any: Throwable) {
            meterRegistry.incrementForProcessing(NotificationsMetrics.event_processing_channel_error, item)
            recordError(
                recordId = recordId,
                item = item,
                validatedConfig = config.asJson(),
                error = any,
                output = output, // Using the current output, even for errors
            )
            NotificationResult.error(any.message ?: any::class.java.name, output)
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