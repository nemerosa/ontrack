package net.nemerosa.ontrack.model.labels

/**
 * Configuration which describes the list of build links
 * to display, based on some project labels.
 *
 * @property labels List of project labels to keep as "main" dependencies
 * @property order Order of priority
 * @property override If this configuration overrides the previous ones
 */
class ProvidedMainBuildLinksConfig(
        val labels: List<String>,
        val order: Int,
        val override: Boolean
) {
    companion object {
        /**
         * Hint for a global configuration
         */
        const val GLOBAL = 10
        /**
         * Hint for a project configuration
         */
        const val PROJECT = 100
    }
}
