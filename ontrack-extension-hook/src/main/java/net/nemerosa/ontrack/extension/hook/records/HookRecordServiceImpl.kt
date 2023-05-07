package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import net.nemerosa.ontrack.extension.recordings.RecordingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class HookRecordServiceImpl(
        private val hookRecordingsExtension: HookRecordingsExtension,
        private val recordingsService: RecordingsService,
) : HookRecordService {

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun onReceived(hook: String, request: HookRequest): String {
        val record = HookRecord(
                id = UUID.randomUUID().toString(),
                hook = hook,
                request = request.obfuscate(), // Removing all headers
                startTime = Time.now(),
                state = HookRecordState.RECEIVED,
                message = null,
                exception = null,
                endTime = null,
                response = null,
        )
        recordingsService.record(hookRecordingsExtension, record)
        return record.id
    }

    override fun onUndefined(recordId: String) {
        recordingsService.updateRecord(hookRecordingsExtension, recordId) {
            it.withState(HookRecordState.UNDEFINED)
                    .withEndTime()
                    .withMessage("The hook is not defined")
        }
    }

    override fun onDisabled(recordId: String) {
        recordingsService.updateRecord(hookRecordingsExtension, recordId) {
            it.withState(HookRecordState.DISABLED)
                    .withEndTime()
                    .withMessage("The hook is disabled")
        }
    }

    override fun onDenied(recordId: String) {
        recordingsService.updateRecord(hookRecordingsExtension, recordId) {
            it.withState(HookRecordState.DENIED)
                    .withEndTime()
                    .withMessage("The access to this hook has been denied")
        }
    }

    override fun onSuccess(recordId: String, result: HookResponse) {
        recordingsService.updateRecord(hookRecordingsExtension, recordId) {
            it.withState(HookRecordState.SUCCESS)
                    .withEndTime()
                    .withResponse(result)
        }
    }

    override fun onError(recordId: String, exception: Exception) {
        recordingsService.updateRecord(hookRecordingsExtension, recordId) {
            it.withState(HookRecordState.ERROR)
                    .withEndTime()
                    .withException(exception)
        }
    }
}