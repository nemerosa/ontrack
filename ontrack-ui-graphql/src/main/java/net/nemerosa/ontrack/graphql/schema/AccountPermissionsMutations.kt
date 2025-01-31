package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.security.PermissionInput
import net.nemerosa.ontrack.model.security.PermissionTargetType
import org.springframework.stereotype.Component

@Component
class AccountPermissionsMutations(
    private val accountService: AccountService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        unitMutation(
            name = "grantGlobalRoleToAccount",
            description = "Grants a global role to an account",
            input = GrantGlobalRoleToAccountInput::class,
        ) { input ->
            accountService.saveGlobalPermission(
                PermissionTargetType.ACCOUNT,
                input.accountId,
                PermissionInput(role = input.globalRole)
            )
        }
    )
}

data class GrantGlobalRoleToAccountInput(
    val accountId: Int,
    val globalRole: String,
)
