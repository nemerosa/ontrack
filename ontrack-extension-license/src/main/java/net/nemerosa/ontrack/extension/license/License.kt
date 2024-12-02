package net.nemerosa.ontrack.extension.license

import net.nemerosa.ontrack.common.Time
import java.time.LocalDateTime

/**
 * License information.
 *
 * @property type Type of license
 * @property name Display name for the license
 * @property assignee Name of the assignee
 * @property active Is the license active?
 * @property validUntil End of validity for this license (null for unlimited)
 * @property maxProjects Maximum number of projects which can be created (0 for unlimited)
 * @property features List of features being allowed
 */
data class License(
    val type: String,
    val name: String,
    val assignee: String,
    val active: Boolean,
    val validUntil: LocalDateTime?,
    val maxProjects: Int,
    val features: List<LicenseFeatureData>,
) {

    private val featureIndex = features.associateBy { it.id }

    fun isValid() = active && (validUntil == null || validUntil > Time.now)

    fun findFeatureData(id: String) = featureIndex[id]

    fun isFeatureEnabled(id: String) = findFeatureData(id)?.enabled == true

}
