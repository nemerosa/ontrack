package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountService
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.stereotype.Component

@Component
class AccountMutations(
    private val accountService: AccountService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        /**
         * Disabling an account
         */
        simpleMutation("disableAccount", "Disables an account",
            DisableAccountInput::class,
            "account", "Updated account", Account::class) { input ->
            val id = ID.of(input.id)
            accountService.setAccountDisabled(id, true)
            accountService.getAccount(id)
        },
        /**
         * Enabling an account
         */
        simpleMutation("enableAccount", "Enables an account",
            EnableAccountInput::class,
            "account", "Updated account", Account::class) { input ->
            val id = ID.of(input.id)
            accountService.setAccountDisabled(id, false)
            accountService.getAccount(id)
        },
        /**
         * Locking an account
         */
        simpleMutation("lockAccount", "Locks an account",
            LockAccountInput::class,
            "account", "Updated account", Account::class) { input ->
            val id = ID.of(input.id)
            accountService.setAccountLocked(id, true)
            accountService.getAccount(id)
        },
        /**
         * Unlocking an account
         */
        simpleMutation("unlockAccount", "Unlocks an account",
            UnlockAccountInput::class,
            "account", "Updated account", Account::class) { input ->
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
