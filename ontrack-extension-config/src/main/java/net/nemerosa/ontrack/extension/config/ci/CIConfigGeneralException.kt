package net.nemerosa.ontrack.extension.config.ci

import net.nemerosa.ontrack.common.UserException

class CIConfigGeneralException(
    message: String,
    exception: Exception? = null,
) : UserException(message, exception)
