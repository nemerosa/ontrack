package net.nemerosa.ontrack.model.extension

data class ExtensionFeatureDescription(
        val id: String,
        val name: String,
        val description: String,
        val version: String,
        val options: ExtensionFeatureOptions
) {
    fun toProduction() = ExtensionFeatureDescription(
        id = id,
        name = name,
        description = description,
        version = version,
        options = options.toProduction(),
    )
}
