package net.nemerosa.ontrack.kdsl.spec.extension.tfc

import net.nemerosa.ontrack.kdsl.spec.Configuration

data class TFCConfiguration(
    override val name: String,
    val url: String,
    val token: String,
) : Configuration
