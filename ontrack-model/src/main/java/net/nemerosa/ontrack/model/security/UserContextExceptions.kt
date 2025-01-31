package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.common.BaseException

class NoUserContextException : BaseException(
    """No security context found to created the user context"""
)

class NotAuthenticatedUserContextException : BaseException(
    """No authenticated security context found to created the user context"""
)

class MismatchAuthenticatedUserContextException(principal: Any) : BaseException(
    """Expected an Ontrack authenticated user but found [${principal::class.java.simpleName}]"""
)
