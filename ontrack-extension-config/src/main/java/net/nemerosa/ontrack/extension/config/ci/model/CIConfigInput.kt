package net.nemerosa.ontrack.extension.config.ci.model

data class CIConfigInput(
    val version: String,
    val configuration: CIConfigRoot = CIConfigRoot(),
)