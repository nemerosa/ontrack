package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.common.BaseException

/**
 * Thrown when a [VersionSource] cannot get a version.
 */
class VersionSourceNoVersionException(message: String) : BaseException(message)
