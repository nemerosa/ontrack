package net.nemerosa.ontrack.extension.general

/**
 * List of the promotions a promotion depends on.
 *
 * @property dependencies List of the names of the promotions a promotion depends on.
 */
data class PromotionDependenciesProperty(
        val dependencies: List<String>
)
