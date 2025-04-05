package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountLoginService
import net.nemerosa.ontrack.model.security.BuiltinAuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountLoginServiceImpl(
    private val accountRepository: AccountRepository,
    private val builtinAuthenticationSourceProvider: BuiltinAuthenticationSourceProvider,
) : AccountLoginService {

    override fun login(email: String, fullName: String): Account? {
        return accountRepository.findOrCreateAccount(
            Account(
                id = ID.NONE,
                name = email,
                fullName = fullName,
                email = email,
                authenticationSource = builtinAuthenticationSourceProvider.source,
                role = SecurityRole.USER,
                disabled = false,
                locked = false,
            )
        )
    }

}