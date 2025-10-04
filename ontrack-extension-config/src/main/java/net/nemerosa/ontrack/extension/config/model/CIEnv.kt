package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("CI environment variable")
data class CIEnv(
    @APIDescription("Name of the variable")
    val name: String,
    @APIDescription("Value of the variable")
    val value: String,
)