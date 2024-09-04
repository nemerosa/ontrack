package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.common.BaseException

/**
 * Processing exception thrown if no version can be identified for a specific path.
 */
class AutoVersioningCustomVersionNotFoundException(
    path: String,
    versionSource: String,
) : BaseException(
    """Cannot find version in "$path" using version source "$versionSource""""
)