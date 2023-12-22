package net.nemerosa.ontrack.model.structure

/**
 * Change for a given link (project x qualifier)
 *
 * @property project Linked project
 * @property qualifier Linked qualifier (can be empty, not null)
 */
data class LinkChange(
    val project: Project,
    val qualifier: String,
    val from: Build?,
    val to: Build?,
)
