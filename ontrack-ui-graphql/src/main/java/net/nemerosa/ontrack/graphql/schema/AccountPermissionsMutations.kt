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
        },
        unitMutation(
            name = "grantGlobalRoleToAccountGroup",
            description = "Grants a global role to an account group",
            input = GrantGlobalRoleToAccountGroupInput::class,
        ) { input ->
            accountService.saveGlobalPermission(
                PermissionTargetType.GROUP,
                input.accountGroupId,
                PermissionInput(role = input.globalRole)
            )
        },
        unitMutation(
            name = "deleteGlobalRoleFromAccount",
            description = "Removes any global role from an account",
            input = DeleteGlobalRoleFromAccountInput::class,
        ) { input ->
            accountService.deleteGlobalPermission(
                PermissionTargetType.ACCOUNT,
                input.accountId,
            )
        },
        unitMutation(
            name = "deleteGlobalRoleFromAccountGroup",
            description = "Removes any global role from an account group",
            input = DeleteGlobalRoleFromAccountGroupInput::class,
        ) { input ->
            accountService.deleteGlobalPermission(
                PermissionTargetType.GROUP,
                input.accountGroupId,
            )
        },
    )
}

data class GrantGlobalRoleToAccountInput(
    val accountId: Int,
    val globalRole: String,
)

data class GrantGlobalRoleToAccountGroupInput(
    val accountGroupId: Int,
    val globalRole: String,
)

data class DeleteGlobalRoleFromAccountInput(
    val accountId: Int,
)

data class DeleteGlobalRoleFromAccountGroupInput(
    val accountGroupId: Int,
)
