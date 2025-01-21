package net.nemerosa.ontrack.extension.scm.files

import net.nemerosa.ontrack.common.BaseException

class SCMRefUnknownSCMTypeException(type: String): BaseException(
    """SCM type is not supported: $type."""
)