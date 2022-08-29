package net.nemerosa.ontrack.extension.license

import java.time.LocalDateTime

/**
 * License information.
 *
 * @property name Type of license, display name, description, etc.
 * @property assignee Name of the assignee
 * @property active Is the license active?
 * @property validUntil End of validity for this license (null for unlimited)
 * @property maxProjects Maximum number of projects which can be created (0 for unlimited)
 */
data class License(
    val name: String,
    val assignee: String,
    val active: Boolean,
    val validUntil: LocalDateTime?,
    val maxProjects: Int,
)
