package net.nemerosa.ontrack.model.extension;

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Options for a feature
 *
 * @property isGui Does the extension provides some web components?
 * @property dependencies List of extensions IDs this feature depends on.
 */
class ExtensionFeatureOptions(
        @JsonProperty("gui")
        val isGui: Boolean = false,
        val dependencies: Set<String>
) {

    companion object {
        /**
         * Default options
         */
        @JvmField
        val DEFAULT = ExtensionFeatureOptions(
                isGui = false,
                dependencies = emptySet()
        )
    }

    /**
     * With GUI
     */
    fun withGui(isGui: Boolean) = ExtensionFeatureOptions(isGui, dependencies)

    /**
     * List of extensions IDs this feature depends on.
     */
    fun withDependencies(dependencies: Set<String>) = ExtensionFeatureOptions(isGui, dependencies)

    /**
     * Adds a dependency
     */
    fun withDependency(feature: ExtensionFeature) = ExtensionFeatureOptions(
            isGui,
            dependencies + feature.id
    )

}
