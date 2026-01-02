package net.nemerosa.ontrack.model.extension

/**
 * List of all extensions, and the relationship between them.
 */
data class ExtensionList(
    /**
     * Raw list of extensions
     */
    val extensions: List<ExtensionFeatureDescription>
)
