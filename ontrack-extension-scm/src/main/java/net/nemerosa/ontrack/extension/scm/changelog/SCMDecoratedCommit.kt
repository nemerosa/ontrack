package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.structure.Project

/**
 * Commit decorated with additional information.
 *
 * @property project Linked project
 * @property commit Underlying commit
 */
data class SCMDecoratedCommit(
    val project: Project,
    val commit: SCMCommit,
)