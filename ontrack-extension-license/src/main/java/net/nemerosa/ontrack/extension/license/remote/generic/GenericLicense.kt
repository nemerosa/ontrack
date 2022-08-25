package net.nemerosa.ontrack.extension.license.remote.generic

import java.time.LocalDateTime

/**
 * License information.
 *
 * @property name Type of license, display name, description, etc.
 * @property assignee Name of the assignee
 * @property validUntil End of validity for this license (null for unlimited)
 * @property maxProjects Maximum number of projects which can be created (0 for unlimited)
 */
data class GenericLicense(
    val name: String,
    val assignee: String,
    val validUntil: LocalDateTime?,
    val maxProjects: Int,
)
