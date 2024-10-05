package net.nemerosa.ontrack.extensions.environments.storage

import net.nemerosa.ontrack.common.BaseException

class EnvironmentIdNotFoundException(id: String) : BaseException(
    "Environment ID '$id' not found"
)
