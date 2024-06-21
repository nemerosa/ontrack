package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription

data class SinceTemplatingFunctionParameters(
    @APIDescription("How to render the period. Supported values are: seconds, millis. Defaults to seconds.")
    val format: String? = null,
    @APIDescription("Origin time. Expression which must be rendered as a date/time")
    val from: String,
    @APIDescription("Last time. Expression which is must be rendered as a date/time. Defaults to current time")
    val ref: String? = null,
)
