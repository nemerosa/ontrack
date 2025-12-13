package net.nemerosa.ontrack.graphql.schema.security

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountGroupInput
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component

@Component
class AccountMutations(
    private val accountService: AccountService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(

        /**
         * Editing an account
         */
        unitMutation<EditAccountInput>(
            name = "editAccount",
            description = "Edits an account",
        ) { input ->
            val id = ID.of(input.id)
            val existing = accountService.getAccount(id)
            accountService.updateAccount(
                id,
                AccountInput(
                    email = existing.email,
                    fullName = input.fullName?.takeIf { it.isNotBlank() } ?: existing.fullName,
                    groups = input.groups,
                )
            )
        },

        /**
         * Deleting an account
         */
        unitMutation<DeleteAccountInput>(
            name = "deleteAccount",
            description = "Deletes an account",
        ) { input ->
            val id = ID.of(input.accountId)
            accountService.deleteAccount(id)
        },

        /**
         * Creating an account group
         */
        simpleMutation(
            name = "createAccountGroup",
            description = "Creates an account group",
            input = CreateAccountGroupInput::class,
            outputName = "accountGroup",
            outputType = AccountGroup::class,
            outputDescription = "Created account group",
        ) { input ->
            accountService.createGroup(
                AccountGroupInput(
                    name = input.name,
                    description = input.description,
                )
            )
        },

        /**
         * Editing an account group
         */
        unitMutation<EditAccountGroupInput>(
            name = "editAccountGroup",
            description = "Edits an account group",
        ) { input ->
            val id = ID.of(input.id)
            accountService.updateGroup(
                id,
                AccountGroupInput(
                    name = input.name,
                    description = input.description,
                )
            )
        },

        /**
         * Deleting an account group
         */
        unitMutation<DeleteAccountGroupInput>(
            name = "deleteAccountGroup",
            description = "Deletes an account group",
        ) { input ->
            val id = ID.of(input.id)
            accountService.deleteGroup(id)
        },
    )
}

data class EditAccountInput(
    @APIDescription("ID of the account")
    val id: Int,
    @get:NotNull(message = "The account full name is required.")
    @get:Size(min = 1, max = 100, message = "The account full name must be between 1 and 100 long.")
    @APIDescription("New full name for the account. If null, the full name is not changed.")
    val fullName: String? = null,
    @APIDescription("List of groups the account is a member of. If null, the groups are not changed.")
    @ListRef
    val groups: List<Int>? = null,
)

data class DeleteAccountInput(
    @APIDescription("ID of the account")
    val accountId: Int,
)

@APIDescription("Group to create")
data class CreateAccountGroupInput(
    @get:NotNull(message = "The group name is required.")
    @get:Size(min = 1, max = 100, message = "The group name must be between 1 and 100 long.")
    @APIDescription("Name of the group")
    val name: String,
    @get:Size(min = 0, max = 300, message = "The group description must be between 1 and 300 long.")
    @APIDescription("Description of the group")
    val description: String? = null,
)

data class EditAccountGroupInput(
    @APIDescription("ID of the account group")
    val id: Int,
    @get:NotNull(message = "The group name is required.")
    @get:Size(min = 1, max = 100, message = "The group name must be between 1 and 100 long.")
    @APIDescription("Name of the group")
    val name: String,
    @get:Size(min = 0, max = 300, message = "The group description must be between 1 and 300 long.")
    @APIDescription("Description of the group")
    val description: String? = null,
)

data class DeleteAccountGroupInput(
    @APIDescription("ID of the account group")
    val id: Int,
)
