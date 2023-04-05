package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.HookRequest
import net.nemerosa.ontrack.extension.hook.HookResponse
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.apache.commons.lang3.exception.ExceptionUtils
import java.time.LocalDateTime

@APIDescription("Record for a hook interaction")
data class HookRecord(
        @APIDescription("Unique ID for the hook received message")
        val id: String,
        @APIDescription("Reference to the handler of this hook")
        val hook: String,
        @APIDescription("Body of the hook request")
        @TypeRef
        val request: HookRequest,
        @APIDescription("Start time of the processing")
        val startTime: LocalDateTime,
        @APIDescription("Current state of the processing")
        val state: HookRecordState,
        @APIDescription("Any message associated with the processing")
        val message: String?,
        @APIDescription("Any exception associated with the processing")
        val exception: String?,
        @APIDescription("End time of the processing")
        val endTime: LocalDateTime?,
        @APIDescription("Response returned by the hook")
        @TypeRef
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