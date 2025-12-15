package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.common.UserException

/**
 * Processing exception thrown if no version can be identified in target file using the
 * PR creation configuration.
 */
class AutoVersioningVersionNotFoundException(
    path: String,
) : UserException(
    """Cannot find version in "$path""""
)