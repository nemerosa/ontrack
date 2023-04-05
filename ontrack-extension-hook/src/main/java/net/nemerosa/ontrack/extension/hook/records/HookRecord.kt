package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import org.apache.commons.lang3.exception.ExceptionUtils
import java.lang.Exception
import java.time.LocalDateTime

data class HookRecord(
        val id: String,
        val hook: String,
        val request: HookRequest,
        val startTime: LocalDateTime,
        val state: HookRecordState,
        val message: String?,
        val exception: String?,
        val endTime: LocalDateTime?,
        val response: HookResponse?,
) {
    fun withEndTime() = HookRecord(
            id = id,
            hook = hook,
            request = request,
            startTime = startTime,
            state = state,
            message = message,
            exception = exception,
            endTime = Time.now(),
            response = response
    )

    fun withState(state: HookRecordState) = HookRecord(
            id = id,
            hook = hook,
            request = request,
            startTime = startTime,
            state = state,
            message = message,
            exception = exception,
            endTime = endTime,
            response = response
    )

    fun withMessage(message: String) = HookRecord(
            id = id,
            hook = hook,
            request = request,
            startTime = startTime,
            state = state,
            message = message,
            exception = exception,
            endTime = endTime,
            response = response
    )

    fun withResponse(response: HookResponse) = HookRecord(
            id = id,
            hook = hook,
            request = request,
            startTime = startTime,
            state = state,
            message = message,
            exception = exception,
            endTime = endTime,
            response = response
    )

    fun withException(exception: Exception) = HookRecord(
            id = id,
            hook = hook,
            request = request,
            startTime = startTime,
            state = state,
            message = message,
            exception = reducedStackTrace(exception),
            endTime = endTime,
            response = response
    )

    companion object {
        private const val MAX_STACK_HEIGHT = 20

        fun reducedStackTrace(error: Throwable) =
                ExceptionUtils.getStackFrames(error).take(MAX_STACK_HEIGHT).joinToString("\n")
    }
}