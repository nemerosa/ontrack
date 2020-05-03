package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.BaseException

class AuthenticationSourceNotFoundException(id: String, name: String) : BaseException(
        "Authentication source with provider = $id and name = $name cannot be found."
)