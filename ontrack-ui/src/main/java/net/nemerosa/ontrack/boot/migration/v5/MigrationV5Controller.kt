package net.nemerosa.ontrack.boot.migration.v5

import net.nemerosa.ontrack.model.security.AccountService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Helpers for the V5 migration.
 */
@RestController
@RequestMapping("/rest/migration/v5")
class MigrationV5Controller(
    private val accountService: AccountService,
) {

    @GetMapping("status")
    fun status() = MigrationV5Status(
        users = usersStatus(),
    )

    private fun usersStatus(): MigrationV5UsersStatus {
        // List of all users
        val accounts = accountService.accounts

        // Normal users
        val users = accounts
            .filter { !it.isDefaultAdmin }
            .map {
                MigrationV5User(
                    id = it.id(),
                    email = it.email,
                    fullName = it.fullName,
                    groups = accountService.getGroupsForAccount(it.id).map { group -> group.name },
                )
            }

        // Grouping per email, keeping the most recent user (using ID)
        val emailLists = users.groupBy { it.email }

        val finalUsers = emailLists
            .mapValues { (_, users) -> users.maxBy { it.id } }
            .values
            .sortedBy { it.fullName }

        val conflicts = emailLists.filterValues { it.size > 1 }

        // OK
        return MigrationV5UsersStatus(
            finalUsers = finalUsers,
            conflicts = conflicts,
        )
    }

}