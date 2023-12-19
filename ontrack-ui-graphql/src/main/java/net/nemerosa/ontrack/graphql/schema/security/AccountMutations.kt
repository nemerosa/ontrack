package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountInput
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

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
                    name = input.name,
                    fullName = input.fullName,
                    email = input.email,
                    password = input.password,
                    groups = emptyList(),
                    disabled = false,
                    locked = false,
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
    @get:NotNull(message = "The account password is required.")
    val password: String,
)
