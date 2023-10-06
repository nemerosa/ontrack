package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.model.exceptions.NotFoundException

/**
 * Thrown when a [VersionSource] cannot be found using its [id][VersionSource.id].
 *
 * @param id ID which cannot be found
 */
class VersionSourceNotFoundException(id: String) : NotFoundException(
    """Version source not found: $id"""
)
