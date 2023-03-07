package net.nemerosa.ontrack.model.extension;

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Options for a feature
 *
 * @property isGui Does the extension provides some web components?
 * @property extraJsModules List of extra JS files to load besides the default `module.js` one
 * @property dependencies List of extensions IDs this feature depends on.
 */
class ExtensionFeatureOptions(
    @JsonProperty("gui")
    val isGui: Boolean = false,
    val extraJsModules: List<String>? = null,
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
    fun withGui(isGui: Boolean) = ExtensionFeatureOptions(isGui, extraJsModules, dependencies)

    /**
     * List of extensions IDs this feature depends on.
     */
    fun withDependencies(dependencies: Set<String>) = ExtensionFeatureOptions(isGui, extraJsModules, dependencies)

    /**
     * Adds a dependency
     */
    fun withDependency(feature: ExtensionFeature) = ExtensionFeatureOptions(
        isGui,
        extraJsModules,
        dependencies + feature.id
    )

    /**
     * Adds an extra JS module
     */
    fun withExtraJSModule(name: String) = ExtensionFeatureOptions(
        isGui = isGui,
        extraJsModules = if (extraJsModules == null) {
            listOf(name)
        } else {
            extraJsModules + name
        },
        dependencies = dependencies,
    )

}
