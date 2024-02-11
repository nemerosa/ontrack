package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription

data class LinkTemplatingFunctionParameters(
    @APIDescription("Text of the link. This must be a value which is part of the templating context.")
    val text: String,
    @APIDescription("Address for the link. This must be a value which is part of the templating context.")
    val href: String,
)