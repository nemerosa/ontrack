package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokensService
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.annotation.Selector
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * Actuator endpoint used for testing to provision an account
 * and get a token.
 */
@Component
@Endpoint(id = "account")
class AccountProvisioningEndpoint(
    private val accountService: AccountService,
    private val tokensService: TokensService,
    private val securityService: SecurityService,
) {

    @ReadOperation
    fun provisionAccount(@Selector username: String): String =
        securityService.asAdmin {
            val account = accountService.findAccountByName(username)
                ?: error("Account with name $username not found")
            val tokenName = "token-${Time.now().format(DateTimeFormatter.ISO_DATE_TIME)}"
            val token = tokensService.generateToken(
                accountId = account.id(),
                options = TokenOptions(
                    name = tokenName,
                )
            )
            token.value
        }
}
