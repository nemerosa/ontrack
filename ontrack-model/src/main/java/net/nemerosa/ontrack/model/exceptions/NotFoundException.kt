package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.UserException

abstract class NotFoundException(
        pattern: String,
        vararg parameters: Any
) : UserException(String.format(pattern, *parameters))
