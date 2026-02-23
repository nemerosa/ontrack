package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.BaseException

class ScmSearchIndexProjectTimeoutException(projectName: String) :
    BaseException("Timeout while indexing project: $projectName")
