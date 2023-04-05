package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
class HookRecordServiceImpl(
        val store: HookRecordStore,
) : HookRecordService {

    override fun onReceived(hook: String, request: HookRequest): String {
        val record = HookRecord(
                id = UUID.randomUUID().toString(),
                hook = hook,
                request = request,
                startTime = Time.now(),
                state = HookRecordState.RECEIVED,
                message = null,
                exception = null,
                endTime = null,
                response = null,
        )
        store.save(record)
        return record.id
    }

    override fun onUndefined(recordId: String) {
        store.save(recordId) {
            it.withState(HookRecordState.UNDEFINED)
                    .withEndTime()
                    .withMessage("The hook is not defined")
        }
    }

    override fun onDisabled(recordId: String) {
        store.save(recordId) {
            it.withState(HookRecordState.DISABLED)
                    .withEndTime()
                    .withMessage("The hook is disabled")
        }
    }

    override fun onDenied(recordId: String) {
        store.save(recordId) {
            it.withState(HookRecordState.DENIED)
                    .withEndTime()
                    .withMessage("The access to this hook has been denied")
        }
    }

    override fun onSuccess(recordId: String, result: HookResponse) {
        store.save(recordId) {
            it.withState(HookRecordState.SUCCESS)
                    .withEndTime()
                    .withResponse(result)
        }
    }

    override fun onError(recordId: String, exception: Exception) {
        store.save(recordId) {
            it.withState(HookRecordState.ERROR)
                    .withEndTime()
                    .withException(exception)
        }
    }
}