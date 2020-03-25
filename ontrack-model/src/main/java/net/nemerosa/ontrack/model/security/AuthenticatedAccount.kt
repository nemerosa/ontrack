package net.nemerosa.ontrack.model.security

import org.springframework.security.core.userdetails.UserDetails

class AuthenticatedAccount(val account: Account, val userDetails: UserDetails) {
    companion object {
        @JvmStatic
        fun of(account: Account): AuthenticatedAccount {
            return AuthenticatedAccount(account, AccountUserDetails(account))
        }
    }

}