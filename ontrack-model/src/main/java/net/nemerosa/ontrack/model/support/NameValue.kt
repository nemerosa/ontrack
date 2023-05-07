package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Association of a name with a value")
data class NameValue(
        @APIDescription("Name of the pair")
        val name: String,
        @APIDescription("Value of the pair")
        val value: String,
)
