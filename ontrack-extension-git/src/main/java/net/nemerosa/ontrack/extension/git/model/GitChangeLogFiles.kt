package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.annotations.APIDescription

class GitChangeLogFiles(
        @APIDescription("List of individual file changes")
        val list: List<GitChangeLogFile>
)
