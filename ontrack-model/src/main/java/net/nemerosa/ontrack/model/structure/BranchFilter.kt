package net.nemerosa.ontrack.model.structure

/**
 * Filter to use when getting the branches for a project.
 *
 * @property name Regular expression to filter on the branch Ontrack name
 * @property favorite Checking if the branch must be a favorite
 * @property count Maximum number of branches to return
 * @property order Must the branches be sorted starting from the branch with the most recent build?
 */
data class BranchFilter(
    val name: String? = null,
    val favorite: Boolean? = null,
    val count: Int? = null,
    val order: Boolean = false,
)