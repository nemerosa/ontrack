package net.nemerosa.ontrack.model.extension

data class ExtensionFeatureDescription(
        val id: String,
        val name: String,
        val description: String,
        val version: String,
        val options: ExtensionFeatureOptions
)
