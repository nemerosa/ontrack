package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroupService
import net.nemerosa.ontrack.model.security.AccountGroups
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.AccountIdpGroupRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountGroupServiceImpl(
    private val accountGroupRepository: AccountGroupRepository,
    private val accountIdpGroupRepository: AccountIdpGroupRepository,
    private val groupMappingService: GroupMappingService,
) : AccountGroupService {


    override fun getAccountGroups(account: Account): AccountGroups {
        // Assigned groups
        val assignedGroups = accountGroupRepository.findByAccount(account.id()).toList()
        // Idp groups
        val idpGroups = accountIdpGroupRepository.getAccountIdpGroups(account.id())
        // Mapped groups
        val mappedGroups = idpGroups.mapNotNull { idpGroup ->
            groupMappingService.getMappedGroup(idpGroup)
        }
        // OK
        return AccountGroups(
            assignedGroups = assignedGroups,
            mappedGroups = mappedGroups,
            idpGroups = idpGroups,
        )
    }
}