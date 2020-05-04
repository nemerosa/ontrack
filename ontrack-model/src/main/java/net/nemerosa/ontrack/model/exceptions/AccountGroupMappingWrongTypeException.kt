package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.model.security.AuthenticationSource

class AccountGroupMappingWrongTypeException(
        expectedSource: AuthenticationSource,
        actualSource: AuthenticationSource
) : BaseException("Expected $expectedSource mapping but was $actualSource")