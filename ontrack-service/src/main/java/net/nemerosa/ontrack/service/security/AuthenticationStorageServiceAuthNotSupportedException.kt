package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.model.security.AuthenticatedUser

class AuthenticationStorageServiceAuthNotSupportedException(user: AuthenticatedUser) :
    BaseException("Authentication user type not supported: ${user::class}")
