package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.model.pagination.PaginatedList

interface RecordingsQueryService {

    /**
     * Gets a record by ID
     */
    fun <R : Recording, F : Any> findById(
            extension: RecordingsExtension<R, F>,
            id: String,
    ): R?

    /**
     * Gets a paginated list of recordings.
     */
    fun <R : Recording, F : Any> findByFilter(
            extension: RecordingsExtension<R, F>,
            filter: F?,
            offset: Int,
            size: Int,
    ): PaginatedList<R>

}