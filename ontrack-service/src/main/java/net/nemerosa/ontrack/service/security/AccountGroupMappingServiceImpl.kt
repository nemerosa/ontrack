package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountGroupMappingWrongTypeException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.repository.AccountGroupMappingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountGroupMappingServiceImpl(
        private val accountGroupMappingRepository: AccountGroupMappingRepository
) : AccountGroupMappingService {

    override fun getGroups(authenticationSource: AuthenticationSource, mappedName: String): Collection<AccountGroup> =
            accountGroupMappingRepository.getGroups(authenticationSource, mappedName)

    override val mappings: List<AccountGroupMapping>
        get() = accountGroupMappingRepository.findAll()

    override fun getMappings(authenticationSource: AuthenticationSource): List<AccountGroupMapping> =
            accountGroupMappingRepository.getMappings(authenticationSource)

    override fun newMapping(authenticationSource: AuthenticationSource, input: AccountGroupMappingInput): AccountGroupMapping =
            accountGroupMappingRepository.newMapping(authenticationSource, input)

    override fun getMapping(authenticationSource: AuthenticationSource, id: ID): AccountGroupMapping {
        val o = accountGroupMappingRepository.getMapping(id)
        return if (authenticationSource sameThan o.authenticationSource) {
            o
        } else {
            throw AccountGroupMappingWrongTypeException(authenticationSource, o.authenticationSource)
        }
    }

    override fun updateMapping(authenticationSource: AuthenticationSource, id: ID, input: AccountGroupMappingInput): AccountGroupMapping {
        getMapping(authenticationSource, id)
        return accountGroupMappingRepository.updateMapping(id, input)
    }

    override fun deleteMapping(authenticationSource: AuthenticationSource, id: ID): Ack {
        getMapping(authenticationSource, id)
        return accountGroupMappingRepository.deleteMapping(id)
    }

    override fun getMappingsForGroup(group: AccountGroup): List<AccountGroupMapping> {
        return accountGroupMappingRepository.getMappingsForGroup(group)
    }

}