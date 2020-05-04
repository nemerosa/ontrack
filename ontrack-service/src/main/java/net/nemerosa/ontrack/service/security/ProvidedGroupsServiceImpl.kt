package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.ProvidedGroupsService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.repository.ProvidedGroupsRepository
import org.springframework.stereotype.Service

@Service
class ProvidedGroupsServiceImpl(
        private val securityService: SecurityService,
        private val providedGroupsRepository: ProvidedGroupsRepository
) : ProvidedGroupsService {

    override fun saveProvidedGroups(account: Int, source: AuthenticationSource, groups: Set<String>) {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        providedGroupsRepository.saveProvidedGroups(account, source, groups)
    }

    override fun getProvidedGroups(account: Int, source: AuthenticationSource): Set<String> {
        return providedGroupsRepository.getProvidedGroups(account, source)
    }

    override fun getSuggestedGroups(source: AuthenticationSource, token: String): List<String> {
        return providedGroupsRepository.getSuggestedGroups(source, token)
    }
}