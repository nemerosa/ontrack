package net.nemerosa.ontrack.job

data class JobRunProgress(
    val percentage: Int,
    val message: String,
) {
    val text: String
        get() = if (percentage >= 0) {
            "$message ($percentage%)"
        } else {
            message
        }

    companion object {
        @JvmStatic
        fun messageOnly(message: String) = JobRunProgress(
            percentage = -1,
            message = message,
        )
    }
}
