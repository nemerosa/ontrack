package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList

interface HookRecordQueryService {

    fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord>

    fun deleteByFilter(filter: HookRecordQueryFilter)
}