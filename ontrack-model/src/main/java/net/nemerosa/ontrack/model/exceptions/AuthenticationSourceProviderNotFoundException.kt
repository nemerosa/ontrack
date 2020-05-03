package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.BaseException

class AuthenticationSourceProviderNotFoundException(id: String) : BaseException(
        "Authentication source provider with ID $id cannot be found."
)
