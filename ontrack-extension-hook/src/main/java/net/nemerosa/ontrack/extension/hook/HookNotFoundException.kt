package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class HookNotFoundException(hook: String) : NotFoundException(
    """Hook `$hook` not found."""
)
