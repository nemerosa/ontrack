package net.nemerosa.ontrack.model.labels

/**
 * Configuration which describes the list of build links
 * to display, based on some project labels.
 *
 * @property labels List of project labels to keep as "main" dependencies
 */
class MainBuildLinksConfig(
        val labels: List<String>
) {
    fun merge(o: MainBuildLinksConfig) = MainBuildLinksConfig(
            (labels + o.labels).distinct()
    )
}
