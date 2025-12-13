package net.nemerosa.ontrack.model.security

interface AuthenticationUserService {

    fun createAuthenticatedUser(account: Account): AccountAuthenticatedUser
    fun asUser(account: Account)

}