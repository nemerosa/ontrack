package net.nemerosa.ontrack.job

import java.time.LocalDateTime

open class JobStatus(
        val id: Long,
        val key: JobKey,
        val schedule: Schedule,
        val actualSchedule: Schedule,
        val description: String,
        val isRunning: Boolean,
        val isValid: Boolean,
        val isPaused: Boolean,
        val isDisabled: Boolean,
        val progress: JobRunProgress?,
        val runCount: Long,
        val lastRunDate: LocalDateTime?,
        val lastRunDurationMs: Long,
        val nextRunDate: LocalDateTime?,
        val lastErrorCount: Long,
        val lastError: String?
) {
    val state: JobState
        get() = if (isRunning) {
            JobState.RUNNING
        } else if (!isValid) {
            JobState.INVALID
        } else if (isDisabled) {
            JobState.DISABLED
        } else if (isPaused) {
            JobState.PAUSED
        } else {
            JobState.IDLE
        }

    val isError: Boolean
        get() = lastErrorCount > 0

    val progressText: String
        get() = progress?.text ?: ""

    fun canRun(): Boolean {
        return (!isRunning
                && !isDisabled
                && isValid)
    }

    fun canPause(): Boolean {
        return (schedule.period > 0
                && !isPaused
                && !isDisabled
                && isValid)
    }

    fun canResume(): Boolean {
        return (isPaused
                && !isDisabled
                && isValid)
    }

    fun canBeDeleted(): Boolean {
        return !isValid
    }

    fun canBeStopped(): Boolean {
        return isRunning
    }
}
