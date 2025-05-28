package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccountMutationsIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Editing an account`() {
        val group1 = doCreateAccountGroup()
        val group2 = doCreateAccountGroup()

        val account = doCreateAccount(group1)

        run(
            """
            mutation {
                editAccount(input: {
                    id: ${account.id},
                    fullName: "New full name",
                    groups: [${group2.id}],
                }) {
                    errors {
                        message
                    }
                }
            }
        """.trimIndent()
        ) { data ->
            checkGraphQLUserErrors(data, "editAccount")
        }

        val savedAccount = accountService.getAccount(account.id)
        assertEquals("New full name", savedAccount.fullName)
        assertEquals(
            setOf(group2.id()),
            accountService.getGroupsForAccount(savedAccount.id).map { it.id() }.toSet(),
            "Account groups updated"
        )
    }

    @Test
    @AsAdminTest
    fun `Deleting an account`() {
        val account = doCreateAccount()

        run(
            """
            mutation {
                deleteAccount(input: {
                    accountId: ${account.id}
                }) {
                    errors {
                        message
                    }
                }
            }
        """.trimIndent()
        ) { data ->
            checkGraphQLUserErrors(data, "deleteAccount")
        }

        assertNull(
            accountService.findAccountByName(account.email),
            "Account has been deleted"
        )
    }

    @Test
    @AsAdminTest
    fun `Creating an account group`() {
        val name = uid("g-")
        run(
            """
            mutation {
                createAccountGroup(input: {
                    name: "$name",
                    description: "Some description"
                }) {
                    accountGroup {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """.trimIndent()
        ) { data ->
            checkGraphQLUserErrors(data, "createAccountGroup") { node ->
                val id = node.path("accountGroup").path("id").asInt()
                val savedGroup = accountService.getAccountGroup(ID.of(id))
                assertEquals(name, savedGroup.name)
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Editing an account group`() {
        val group = doCreateAccountGroup()
        val name = uid("g-")
        run(
            """
            mutation {
                editAccountGroup(input: {
                    id: ${group.id},
                    name: "$name",
                    description: "Some description",
                }) {
                    errors {
                        message
                    }
                }
            }
        """.trimIndent()
        ) { data ->
            checkGraphQLUserErrors(data, "editAccountGroup")
        }

        val savedAccountGroup = accountService.getAccountGroup(group.id)
        assertEquals(name, savedAccountGroup.name)
        assertEquals("Some description", savedAccountGroup.description)
    }

    @Test
    @AsAdminTest
    fun `Deleting an account group`() {
        val accountGroup = doCreateAccountGroup()

        run(
            """
            mutation {
                deleteAccountGroup(input: {
                    id: ${accountGroup.id}
                }) {
                    errors {
                        message
                    }
                }
            }
        """.trimIndent()
        ) { data ->
            checkGraphQLUserErrors(data, "deleteAccountGroup")
        }

        assertNull(
            accountService.findAccountGroupByName(accountGroup.name),
            "Account group has been deleted"
        )
    }

}