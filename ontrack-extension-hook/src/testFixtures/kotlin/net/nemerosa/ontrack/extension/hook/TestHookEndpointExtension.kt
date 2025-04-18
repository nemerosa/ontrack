package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.TokenOptions
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import java.util.*

@Component
class TestHookEndpointExtension(
    extension: TestExtensionFeature,
    private val testHookInfoLinkExtension: TestHookInfoLinkExtension,
    private val ontrackConfigProperties: OntrackConfigProperties,
    private val accountService: AccountService,
    private val securityService: SecurityService,
    private val tokensService: TokensService,
) : AbstractExtension(extension), HookEndpointExtension {

    override var enabled: Boolean = true
    var denied: Boolean = false
    var error: Boolean = false

    private var token: String = ""

    override val id: String = "test"

    fun provisionToken(token: String?) {
        if (token.isNullOrBlank()) {
            securityService.asAdmin {
                val account = accountService.findAccountByName(
                    ontrackConfigProperties.security.authorization.admin.email,
                ) ?: error("Account not found: ${ontrackConfigProperties.security.authorization.admin.email}")
                val result = tokensService.generateToken(
                    accountId = account.id(),
                    options = TokenOptions(
                        name = UUID.randomUUID().toString(),
                    )
                )
                this.token = result.value
            }
        } else {
            this.token = token
        }
    }

    override fun checkAccess(request: HookRequest): String {
        if (denied) {
            throw AccessDeniedException("Hook refused the connection.")
        }
        return token
    }

    override fun process(recordId: String, request: HookRequest): HookResponse {
        if (error) {
            throw RuntimeException("Error during the processing.")
        } else {
            return HookResponse(
                type = HookResponseType.PROCESSED,
                info = null,
                infoLink = testHookInfoLinkExtension.createHookInfoLink(
                    "Processing: ${request.body}"
                ),
            )
        }
    }

}