package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("Association of a name with a value")
data class NameValue(
    @APIDescription("Name")
    val name: String,
    @APIDescription("Value")
    val value: String,
)
