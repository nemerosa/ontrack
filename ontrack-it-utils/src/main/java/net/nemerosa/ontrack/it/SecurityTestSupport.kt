package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component

@Component
class SecurityTestSupport(
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountACLService: AccountACLService,
    private val accountGroupRepository: AccountGroupRepository,
    private val accountRepository: AccountRepository,
) {

    fun setupSecurityContext(
        user: AuthenticatedUser,
        securityRole: SecurityRole = SecurityRole.USER,
    ): SecurityContext {
        val authentication = AuthenticatedUserAuthentication(
            authenticatedUser = user,
            authorities = AuthorityUtils.createAuthorityList(securityRole.name)
        )
        val oldContext = SecurityContextHolder.getContext()
        val context: SecurityContext = SecurityContextImpl(authentication)
        SecurityContextHolder.setContext(context)
        return oldContext
    }

    fun createAdminAccount(): Account {
        val name = uid("admin-")
        val groupName = ontrackConfigProperties.security.authorization.admin.groupName
        val group = accountGroupRepository.findAccountGroupByName(groupName)
            ?: error("Cannot find group with name $groupName")
        val account = accountRepository.newAccount(
            Account(
                id = ID.NONE,
                name = name,
                fullName = name,
                email = "$name@ontrack.local",
                role = SecurityRole.USER,
            )
        )
        // Account groups
        accountGroupRepository.linkAccountToGroups(account.id(), listOf(group.id()))
        // OK
        return account
    }

    fun createOntrackAuthenticatedUser(account: Account): AuthenticatedUser =
        AccountAuthenticatedUser(
            account = account,
            authorisations = accountACLService.getAuthorizations(account),
            groups = accountACLService.getGroups(account),
        )

}