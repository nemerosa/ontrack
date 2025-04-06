package net.nemerosa.ontrack.model.security

interface AccountSecurityContextService {

    /**
     * Creates a security context for this account and for this code.
     */
    fun withAccount(account: Account, code: () -> Unit)

}
