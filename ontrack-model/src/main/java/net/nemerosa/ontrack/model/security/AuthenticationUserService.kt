package net.nemerosa.ontrack.model.security

interface AuthenticationUserService {

    fun asUser(account: Account)

}