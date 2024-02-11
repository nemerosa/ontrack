package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.BaseException

abstract class SCMException(message: String) : BaseException(message)

class ProjectNoSCMException : SCMException("Project is not associated with any SCM.")
