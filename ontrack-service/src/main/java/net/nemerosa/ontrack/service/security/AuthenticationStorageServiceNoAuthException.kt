package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.BaseException

class AuthenticationStorageServiceNoAuthException: BaseException("No authentication. Cannot store the context")
