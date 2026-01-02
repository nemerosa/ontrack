package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.AuthenticationStorageService

class MockAuthenticationStorageService : AuthenticationStorageService {

    override fun getAccountId(): String = "user@email.com"

    override fun withAccountId(accountId: String, code: () -> Unit) {
        code()
    }
}