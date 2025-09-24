package net.nemerosa.ontrack.extension.scm

import net.nemerosa.ontrack.extension.scm.changelog.SCMDecoratedCommit
import net.nemerosa.ontrack.model.annotations.APIDescription

data class SCMCommitInfo(
    @APIDescription("Associated decorated SCM commit")
    val scmDecoratedCommit: SCMDecoratedCommit,
)