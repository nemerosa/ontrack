package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.templating.TemplatingFunction
import org.springframework.stereotype.Component

@Component
@APIDescription("Displays the current user")
@Documentation(UserTemplatingFunctionParameters::class)
@DocumentationExampleCode(
    """
       #.user 
    """
)
class UserTemplatingFunction(
    private val securityService: SecurityService,
) : TemplatingFunction {

    override val id: String = "user"

    override fun render(
        configMap: Map<String, String>,
        context: Map<String, Any>,
        renderer: EventRenderer,
        expressionResolver: (expression: String) -> String
    ): String {
        val field = configMap[UserTemplatingFunctionParameters::field.name]
            ?.let { UserTemplatingFunctionField.valueOf(it.uppercase()) }
            ?: UserTemplatingFunctionField.NAME
        val account = securityService.currentAccount
        return if (account == null) {
            ""
        } else when (field) {
            UserTemplatingFunctionField.NAME -> account.account.name
            UserTemplatingFunctionField.DISPLAY -> account.account.fullName
            UserTemplatingFunctionField.EMAIL -> account.account.email
        }
    }
}
