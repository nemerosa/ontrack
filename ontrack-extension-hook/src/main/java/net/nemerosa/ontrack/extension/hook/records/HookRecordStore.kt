package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList

interface HookRecordStore {
    fun save(record: HookRecord)
    fun save(recordId: String, code: (HookRecord) -> HookRecord)

    fun findById(id: String): HookRecord?
    fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord>

    fun deleteByFilter(filter: HookRecordQueryFilter)
}