package net.nemerosa.ontrack.service.files

import net.nemerosa.ontrack.common.BaseException

class FileRefUnsupportedProtocolException(protocol: String) : BaseException(
    """Unsupported file ref protocol: $protocol."""
)
