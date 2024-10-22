package net.nemerosa.ontrack.extension.environments.storage

import net.nemerosa.ontrack.common.BaseException

class EnvironmentIdNotFoundException(id: String) : BaseException(
    "Environment ID '$id' not found"
)
