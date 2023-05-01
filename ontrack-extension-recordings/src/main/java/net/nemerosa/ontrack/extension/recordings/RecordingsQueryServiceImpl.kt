package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.extension.recordings.store.RecordingsStore
import net.nemerosa.ontrack.extension.recordings.store.toRecording
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RecordingsQueryServiceImpl(
        private val securityService: SecurityService,
        private val recordingsStore: RecordingsStore,
) : RecordingsQueryService {

    override fun <R : Recording, F : Any> findById(extension: RecordingsExtension<R, F>, id: String): R? {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return recordingsStore.findById(extension.id, id)?.toRecording(extension)
    }

    override fun <R : Recording, F : Any> countByFilter(extension: RecordingsExtension<R, F>, filter: F?): Int {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        val queryVariables = mutableMapOf<String, Any?>()
        val queries = if (filter != null) {
            extension.filterQuery(filter, queryVariables)
        } else {
            emptyList()
        }
        return recordingsStore.countByFilter(extension.id, queries, queryVariables)
    }

    override fun <R : Recording, F : Any> findByFilter(
            extension: RecordingsExtension<R, F>,
            filter: F?,
            offset: Int,
            size: Int,
    ): PaginatedList<R> {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        val queryVariables = mutableMapOf<String, Any?>()
        val queries = if (filter != null) {
            extension.filterQuery(filter, queryVariables)
        } else {
            emptyList()
        }
        return recordingsStore.findByFilter(extension.id, queries, queryVariables, offset, size).map {
            it.toRecording(extension)
        }
    }

}