package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class HookRecordQueryServiceImpl(
        private val store: HookRecordStore,
): HookRecordQueryService {

    override fun findById(id: String): HookRecord? =
            store.findById(id)

    override fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord> =
            store.findByFilter(filter, offset, size)
}