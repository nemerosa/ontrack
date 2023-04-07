package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HookRecordQueryServiceImpl(
        private val securityService: SecurityService,
        private val store: HookRecordStore,
) : HookRecordQueryService {

    override fun findByFilter(filter: HookRecordQueryFilter, offset: Int, size: Int): PaginatedList<HookRecord> {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return store.findByFilter(filter, offset, size)
    }

    override fun deleteByFilter(filter: HookRecordQueryFilter) {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        store.deleteByFilter(filter)
    }

}