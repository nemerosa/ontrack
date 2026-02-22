package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.BaseException

class ScmSearchIndexProjectNotFoundException(projectName: String) : BaseException("Project not found: $projectName")
