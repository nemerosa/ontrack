package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.BaseException

class AuthenticationSourceNotFoundException(provider: String, source: String) : BaseException(
        "Authentication source with provider = $provider and name = $source cannot be found."
)