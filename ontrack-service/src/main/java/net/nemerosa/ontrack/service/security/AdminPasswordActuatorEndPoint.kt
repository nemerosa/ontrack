package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@WebEndpoint(id = "account")
class AdminPasswordActuatorEndPoint(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @WriteOperation
    fun resetPassword(username: String, password: String): Boolean {
        val account = accountRepository.findAccountByName(username)
        return if (account != null && account.authenticationSource.isAllowingPasswordChange) {
            accountRepository.setPassword(account.id(), passwordEncoder.encode(password))
            true
        } else {
            false
        }
    }

}
