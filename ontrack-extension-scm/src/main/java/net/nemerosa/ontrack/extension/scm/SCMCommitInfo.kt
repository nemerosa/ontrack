package net.nemerosa.ontrack.extension.scm

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.scm.changelog.SCMDecoratedCommit

data class SCMCommitInfo(
    @APIDescription("Associated decorated SCM commit")
    val scmDecoratedCommit: SCMDecoratedCommit,
)