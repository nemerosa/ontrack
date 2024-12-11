package net.nemerosa.ontrack.extension.scm.files

import net.nemerosa.ontrack.common.BaseException

class SCMRefParsingException(path: String): BaseException(
    """Cannot parse SCM path: $path"""
)