package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class SCMRefUnknownSCMTypeException(type: String) : NotFoundException(
    """SCM type is not supported: $type."""
)