package net.nemerosa.ontrack.service.templating

data class DatetimeTemplatingFunctionParameters(
    val format: String? = null,
    val timezone: String? = null,
    val years: Long? = null,
    val months: Long? = null,
    val days: Long? = null,
    val hours: Long? = null,
    val minutes: Long? = null,
    val seconds: Long? = null,
)