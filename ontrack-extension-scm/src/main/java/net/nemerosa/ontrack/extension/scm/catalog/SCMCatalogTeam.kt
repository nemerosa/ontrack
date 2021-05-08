package net.nemerosa.ontrack.extension.scm.catalog

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Definition of a team for a SCM catalog entry.
 *
 * @param id Technical ID of the team in the SCM
 * @param name Display name for the team
 * @param description Description for the team
 * @param url URL to the team
 * @param role Role of the team in the repository
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SCMCatalogTeam(
    val id: String,
    val name: String?,
    val description: String?,
    val url: String?,
    val role: String?
)