package net.nemerosa.ontrack.extension.scm.changelog

/**
 * Commit decorated with additional information.
 *
 * @property commit Underlying commit
 */
data class SCMDecoratedCommit(
    val commit: SCMCommit,
)