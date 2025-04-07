package net.nemerosa.ontrack.model.security

interface AccountLoginService {

    fun login(email: String, fullName: String): Account

}