package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.annotations.APIDescription

// TODO #532 Workaround
open class GitChangeLogFiles(
        @APIDescription("List of individual file changes")
        val list: List<GitChangeLogFile>
)
