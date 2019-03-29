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
    fun mergeInto(existing: ProvidedMainBuildLinksConfig): ProvidedMainBuildLinksConfig =
            if (override) {
                this
            } else {
                ProvidedMainBuildLinksConfig(
                        labels = (existing.labels + labels).distinct(),
                        order = order,
                        override = false
                )
            }

    fun toMainBuildLinksConfig() = MainBuildLinksConfig(
            labels = labels
    )

    companion object {
        /**
         * Hint for a system configuration
         */
        private const val SYSTEM = 0
        /**
         * Hint for a global configuration
         */
        const val GLOBAL = 10
        /**
         * Hint for a project configuration
         */
        const val PROJECT = 100
        /**
         * Empty configuration
         */
        val empty = ProvidedMainBuildLinksConfig(
                labels = emptyList(),
                order = SYSTEM,
                override = false
        )
    }
}
