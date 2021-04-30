package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.UserOldPasswordException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.UserService
import net.nemerosa.ontrack.model.support.PasswordChange
import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
        private val securityService: SecurityService,
        private val accountRepository: AccountRepository,
        private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun changePassword(input: PasswordChange): Ack {
        // Checks the account
        val user = securityService.currentAccount
        return if (user == null) {
            throw AccessDeniedException("Must be logged to change password.")
        } else if (!user.account.authenticationSource.isAllowingPasswordChange) {
            throw AccessDeniedException("Password change is not allowed from ontrack.")
        } else if (user.account.locked) {
            throw AccessDeniedException("User is locked.")
        } else {
            val existing = accountRepository.findBuiltinAccount(user.account.name)
            if (existing != null && existing.account.id() == user.account.id()) {
                val matchingOldPassword = passwordEncoder.matches(input.oldPassword, existing.password)
                if (matchingOldPassword) {
                    accountRepository.setPassword(
                            user.account.id(),
                            passwordEncoder.encode(input.newPassword)
                    )
                    Ack.OK
                } else {
                    throw UserOldPasswordException()
                }
            } else {
                throw AccessDeniedException("Cannot find matching user.")
            }
        }
    }

}