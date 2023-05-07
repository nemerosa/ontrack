package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.common.BaseException

class HookSignatureMissingTokenException : BaseException(
    """Hook signature token has not been set."""
)