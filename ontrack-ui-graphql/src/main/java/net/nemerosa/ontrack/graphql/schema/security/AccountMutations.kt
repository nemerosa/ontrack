package net.nemerosa.ontrack.graphql.schema.security

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component

@Component
class AccountMutations(
    private val accountService: AccountService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        /**
         * Creating a built-in account
         */
        simpleMutation(
            "createBuiltInAccount", "Creates a built-in account",
            CreateBuiltInAccountInput::class,
            "account", "Created account", Account::class
        ) { input ->
            accountService.create(
                AccountInput(
                    fullName = input.fullName,
                    email = input.email,
                    groups = emptyList(),
                )
            )
        },
        /**
         * Disabling an account
         */
        simpleMutation(
            "disableAccount", "Disables an account",
            DisableAccountInput::class,
            "account", "Updated account", Account::class
        ) { input ->
            val id = ID.of(input.id)
            accountService.setAccountDisabled(id, true)
            accountService.getAccount(id)
        },
        /**
         * Enabling an account
         */
        simpleMutation(
            "enableAccount", "Enables an account",
            EnableAccountInput::class,
            "account", "Updated account", Account::class
        ) { input ->
            val id = ID.of(input.id)
            accountService.setAccountDisabled(id, false)
            accountService.getAccount(id)
        },
        /**
         * Locking an account
         */
        simpleMutation(
            "lockAccount", "Locks an account",
            LockAccountInput::class,
            "account", "Updated account", Account::class
        ) { input ->
            val id = ID.of(input.id)
            accountService.setAccountLocked(id, true)
            accountService.getAccount(id)
        },
        /**
         * Unlocking an account
         */
        simpleMutation(
            "unlockAccount", "Unlocks an account",
            UnlockAccountInput::class,
            "account", "Updated account", Account::class
        ) { input ->
            val id = ID.of(input.id)
            accountService.setAccountLocked(id, false)
            accountService.getAccount(id)
        },

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

abstract class AbstractAccountInput(
    @APIDescription("ID of the account")
    val id: Int,
)

class DisableAccountInput(id: Int) : AbstractAccountInput(id)
class EnableAccountInput(id: Int) : AbstractAccountInput(id)
class LockAccountInput(id: Int) : AbstractAccountInput(id)
class UnlockAccountInput(id: Int) : AbstractAccountInput(id)

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

data class CreateBuiltInAccountInput(
    @get:NotNull(message = "The account name is required.")
    @get:Pattern(
        regexp = "[a-zA-Z0-9@_.-]+",
        message = "The account name must contain only letters, digits, underscores, @, dashes and dots."
    )
    val name: String,
    @get:NotNull(message = "The account full name is required.")
    @get:Size(min = 1, max = 100, message = "The account full name must be between 1 and 100 long.")
    val fullName: String,
    @get:NotNull(message = "The account email is required.")
    @get:Size(min = 1, max = 200, message = "The account email must be between 1 and 200 long.")
    val email: String,
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
