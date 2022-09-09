package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig

data class JsonAutoVersioningConfigs(
    val configurations: List<JsonAutoVersioningConfig>,
) {
    fun toConfig() = AutoVersioningConfig(
        configurations = configurations.map { it.toConfig() }
    )
}