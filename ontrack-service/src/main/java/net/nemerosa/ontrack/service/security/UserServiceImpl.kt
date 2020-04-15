package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.UserService
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl internal constructor(
        private val securityService: SecurityService,
        private val accountRepository: AccountRepository,
        private val passwordEncoder: PasswordEncoder
) : UserService {

    @Autowired
    constructor(securityService: SecurityService, accountRepository: AccountRepository) : this(
            securityService,
            accountRepository,
            PasswordEncoderFactories.createDelegatingPasswordEncoder()
    )

    override fun changePassword(input: PasswordChange): Ack {
        // Checks the account
        val user = securityService.currentAccount
        return if (user == null) {
            throw AccessDeniedException("Must be logged to change password.")
        } else if (!user.account.authenticationSource.isAllowingPasswordChange) {
            throw AccessDeniedException("Password change is not allowed from ontrack.")
        } else if (!accountRepository.checkPassword(user.id()) { encodedPassword: String? -> passwordEncoder.matches(input.oldPassword, encodedPassword) }) {
            throw UserOldPasswordException()
        } else {
            accountRepository.setPassword(
                    user.id(),
                    passwordEncoder.encode(input.newPassword)
            )
            Ack.OK
        }
    }

}