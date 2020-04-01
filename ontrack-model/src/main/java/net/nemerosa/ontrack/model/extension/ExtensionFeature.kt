package net.nemerosa.ontrack.model.extension

interface ExtensionFeature {

    /**
     * Short ID of this extension
     */
    val id: String

    /**
     * Display name for this extension
     */
    val name: String

    /**
     * Description for this extension
     */
    val description: String

    /**
     * Gets the version of this feature
     */
    val version: String

    val options: ExtensionFeatureOptions
        get() = ExtensionFeatureOptions.DEFAULT

    val featureDescription: ExtensionFeatureDescription
        get() = ExtensionFeatureDescription(
                id,
                name,
                description,
                version,
                options
        )
}