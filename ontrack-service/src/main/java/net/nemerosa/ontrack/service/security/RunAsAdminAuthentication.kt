package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.security.Account.Companion.of
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils

class RunAsAdminAuthentication(
        authenticatedUser: OntrackAuthenticatedUser?
) : AbstractAuthenticationToken(AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.name)) {

    private val account: Account

    override fun getDetails(): Account = account

    override fun isAuthenticated(): Boolean = true

    override fun getCredentials(): Any = ""

    override fun getPrincipal(): Any {
        return DefaultOntrackAuthenticatedUser(
                user = RunAsAdminUser(account),
                authorizedAccount = AuthorizedAccount(
                        account = account,
                        authorisations = object : AuthorisationsCheck {
                            override fun isGranted(fn: Class<out GlobalFunction>): Boolean = true
                            override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = true
                        }
                ),
                groups = emptyList()
        )
    }

    companion object {
        val ADMIN = of(
            "admin",
            "Run-as Admin",
            "",
            SecurityRole.ADMINISTRATOR,
            BuiltinAuthenticationSourceProvider.runAsSource,
            disabled = false,
            locked = false,
        ).withId(ID.of(1))
    }

    init {
        val account = authenticatedUser?.account
        if (account != null) {
            this.account = of(
                    account.name,
                    account.fullName,
                    account.email,
                    SecurityRole.ADMINISTRATOR,
                    account.authenticationSource,
                    disabled = false,
                    locked = false,
            ).withId(account.id)
        } else {
            this.account = ADMIN
        }
    }

    class RunAsAdminUser(account: Account) : OntrackUser {

        override val accountId: Int = account.id()

        override fun getAuthorities(): Collection<GrantedAuthority> =
                AuthorityUtils.createAuthorityList(SecurityRole.ADMINISTRATOR.roleName)

        override fun isEnabled(): Boolean = true

        override fun getUsername(): String = "admin"

        override fun isCredentialsNonExpired(): Boolean = true

        override fun getPassword(): String = ""

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = true

    }
}