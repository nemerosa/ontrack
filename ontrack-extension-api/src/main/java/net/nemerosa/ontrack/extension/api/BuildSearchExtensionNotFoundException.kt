package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.common.BaseException

class BuildSearchExtensionNotFoundException(id: String) : BaseException(
    """Build search extension with id '$id' was not found."""
)
