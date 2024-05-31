package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.model.annotations.APIDescription

class GitCommitProperty(
        @APIDescription("Commit hash")
        val commit: String
)
