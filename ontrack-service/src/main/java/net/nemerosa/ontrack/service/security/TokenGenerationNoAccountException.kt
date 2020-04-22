package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.BaseException

class TokenGenerationNoAccountException : BaseException(
        "No authentication. Cannot generate a token."
)