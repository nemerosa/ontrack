package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.common.BaseException

abstract class SCMChangeLogServiceException(message: String) : BaseException(message)

class SCMChangeLogNotSameProjectException : SCMChangeLogServiceException(
    """The two builds of a change log must be in the same project."""
)

class SCMChangeLogNotEnabledException(project: String) : SCMChangeLogServiceException(
    """The $project project does not use a SCM which is enabled for change logs."""
)

class SCMChangeLogNoCommitException: SCMChangeLogServiceException(
    """One of the builds does not have a commit property."""
)
