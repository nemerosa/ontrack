package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.common.BaseException

class ScmSearchIndexProjectSCMNotSupportedException(projectName: String) :
    BaseException("Project SCM not supported: $projectName")
