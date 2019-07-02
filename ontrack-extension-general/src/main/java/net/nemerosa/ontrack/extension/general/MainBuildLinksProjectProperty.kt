package net.nemerosa.ontrack.extension.general

/**
 * Configuration which describes the list of build links
 * to display, based on some project labels.
 *
 * @property labels List of project labels to keep as "main" dependencies
 * @property overrideGlobal `true` if the project settings override the global settings, without being merged.
 */
class MainBuildLinksProjectProperty(
        val labels: List<String>,
        val overrideGlobal: Boolean
)
