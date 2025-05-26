package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.GroupMappingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GroupMappingServiceImpl(
    private val securityService: SecurityService,
    private val groupMappingRepository: GroupMappingRepository,
    private val accountGroupRepository: AccountGroupRepository,
) : GroupMappingService {

    override fun getMappedGroup(idpGroup: String): AccountGroup? {
        val groupId: Int? = groupMappingRepository.getMappedGroupId(idpGroup)
        return groupId?.let { accountGroupRepository.getById(ID.of(it)) }
    }

    override fun mapGroup(idpGroup: String, accountGroup: AccountGroup?) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        groupMappingRepository.mapGroup(idpGroup, accountGroup?.id())
    }

    override val groupMappings: List<GroupMapping>
        get() = groupMappingRepository.getMappings {
            accountGroupRepository.getById(ID.of(it))
        }
}