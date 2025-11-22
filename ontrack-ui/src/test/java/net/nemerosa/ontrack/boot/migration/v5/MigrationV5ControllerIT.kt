package net.nemerosa.ontrack.boot.migration.v5

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroupInput
import net.nemerosa.ontrack.model.security.AccountInput
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.fail

class MigrationV5ControllerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var controller: MigrationV5Controller

    @Test
    fun `Migration status with list of users and conflicting emails`() {
        asAdmin {

            // Provisioning of the groups
            accountService.accountGroups.forEach { group ->
                accountService.deleteGroup(group.id)
            }
            val adminGroup = accountService.createGroup(AccountGroupInput("Administrators", ""))
            val managerGroup = accountService.createGroup(AccountGroupInput("Managers", ""))

            // Provisioning of the users
            var adminUser: Account? = null
            val adminEmail = "know@yontrack.com"
            accountService.accounts.forEach { account ->
                if (!account.isDefaultAdmin) {
                    accountService.deleteAccount(account.id)
                } else {
                    adminUser = account
                    accountService.updateAccount(
                        account.id,
                        AccountInput(
                            name = "admin",
                            fullName = "Administrator",
                            email = adminEmail,
                            password = "<PASSWORD>",
                            groups = setOf(adminGroup.id()),
                            disabled = false,
                            locked = false,
                        )
                    )
                }
            }
            if (adminUser == null) {
                fail("Cannot find the default admin user")
            } else {
                val otherUser = accountService.create(
                    AccountInput(
                        name = "other",
                        fullName = "Other",
                        email = adminEmail, // Same email
                        password = "<PASSWORD OTHER>",
                        groups = emptyList(),
                        disabled = false,
                        locked = false,
                    )
                )
                val jenkinsUser = accountService.create(
                    AccountInput(
                        name = "jenkins",
                        fullName = "Jenkins",
                        email = adminEmail, // Same email
                        password = "<PASSWORD JENKINS>",
                        groups = emptyList(),
                        disabled = false,
                        locked = false,
                    )
                )
                val managerUser = accountService.create(
                    AccountInput(
                        name = "manager",
                        fullName = "Manager",
                        email = "manager@yontrack.com",
                        password = "<PASSWORD MANAGER>",
                        groups = setOf(managerGroup.id()),
                        disabled = false,
                        locked = false,
                    )
                )

                // Getting the status
                val status = controller.status()

                // Checking the status
                val jenkinsMigratedUser = MigrationV5User(
                    id = jenkinsUser.id(),
                    email = jenkinsUser.email,
                    fullName = jenkinsUser.fullName,
                    groups = emptyList(),
                )
                val otherMigratedUser = MigrationV5User(
                    id = otherUser.id(),
                    email = otherUser.email,
                    fullName = otherUser.fullName,
                    groups = emptyList(),
                )
                assertEquals(
                    MigrationV5Status(
                        users = MigrationV5UsersStatus(
                            finalUsers = listOf(
                                jenkinsMigratedUser,
                                MigrationV5User(
                                    id = managerUser.id(),
                                    email = managerUser.email,
                                    fullName = managerUser.fullName,
                                    groups = listOf(managerGroup.name),
                                ),
                            ),
                            conflicts = mapOf(
                                jenkinsUser.email to listOf(
                                    jenkinsMigratedUser,
                                    otherMigratedUser,
                                )
                            )
                        )
                    ),
                    status
                )
            }
        }
    }

}