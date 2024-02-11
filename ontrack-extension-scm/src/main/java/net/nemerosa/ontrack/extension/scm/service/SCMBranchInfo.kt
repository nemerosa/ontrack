package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * SCM information for a given branch.
 *
 * @property type SCM type (like git, etc.)
 * @property engine SCM engine or flavor (like GitHub, etc.)
 * @property uri URI of the repository
 * @property branch SCM branch
 * @property changeLogs Are change logs enabled for this SCM?
 */
@APIDescription("SCM information for a given branch.")
data class SCMBranchInfo(
    @APIDescription("SCM type (like git, etc.)")
    val type: String,
    @APIDescription("SCM engine or flavor (like GitHub, etc.)")
    val engine: String,
    @APIDescription("URI of the repository")
    val uri: String,
    @APIDescription("SCM branch")
    val branch: String,
    @APIDescription("Are change logs enabled for this SCM?")
    val changeLogs: Boolean?,
)
