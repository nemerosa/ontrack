package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingWrongTypeException
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import net.nemerosa.ontrack.model.security.AccountGroupMappingInput
import net.nemerosa.ontrack.model.security.AccountGroupMappingService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.repository.AccountGroupMappingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountGroupMappingServiceImpl(
        private val accountGroupMappingRepository: AccountGroupMappingRepository
) : AccountGroupMappingService {

    override fun getGroups(mapping: String, mappedName: String): Collection<AccountGroup> =
            accountGroupMappingRepository.getGroups(mapping, mappedName)

    override val mappings: List<AccountGroupMapping>
        get() = accountGroupMappingRepository.findAll()

    override fun getMappings(mapping: String): List<AccountGroupMapping> =
            accountGroupMappingRepository.getMappings(mapping)

    override fun newMapping(mapping: String, input: AccountGroupMappingInput): AccountGroupMapping =
            accountGroupMappingRepository.newMapping(mapping, input)

    override fun getMapping(mapping: String, id: ID): AccountGroupMapping {
        val o = accountGroupMappingRepository.getMapping(id)
        return if (mapping == o.type) {
            o
        } else {
            throw AccountGroupMappingWrongTypeException(mapping, o.type)
        }
    }

    override fun updateMapping(mapping: String, id: ID, input: AccountGroupMappingInput): AccountGroupMapping {
        getMapping(mapping, id)
        return accountGroupMappingRepository.updateMapping(id, input)
    }

    override fun deleteMapping(mapping: String, id: ID): Ack {
        getMapping(mapping, id)
        return accountGroupMappingRepository.deleteMapping(id)
    }

    override fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping> {
        return accountGroupMappingRepository.getMappingsForGroup(group)
    }

}