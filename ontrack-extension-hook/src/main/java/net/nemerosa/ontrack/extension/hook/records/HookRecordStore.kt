package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList
import java.time.LocalDateTime

interface HookRecordStore {
    fun save(record: HookRecord)
    fun save(recordId: String, code: (HookRecord) -> HookRecord)

    fun findById(id: String): HookRecord?
    fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord>

    fun deleteByFilter(filter: HookRecordQueryFilter)

    fun removeAllBefore(retentionDate: LocalDateTime, nonRunningOnly: Boolean): Int
    fun removeAll()
}