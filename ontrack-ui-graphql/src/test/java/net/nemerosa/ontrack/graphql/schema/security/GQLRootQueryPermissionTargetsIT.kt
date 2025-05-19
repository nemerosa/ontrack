package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GQLRootQueryPermissionTargetsIT : AbstractQLKTITSupport() {

    @Test
    @AsAdminTest
    fun `Searching permission targets`() {
        val prefix = uid("p-")
        val account = doCreateAccount(name = uid("$prefix-"))
        val group = doCreateAccountGroup(name = uid("$prefix-"))

        val query = """
            query SearchPermissionTargets(${'$'}token: String!) {
                permissionTargets(token: ${'$'}token) {
                    type
                    id
                    name
                }
            }
        """.trimIndent()

        // Account & group
        run(query, mapOf("token" to prefix)) { data ->
            val targets = data.path("permissionTargets")
            assertNotNull(
                targets.find { it.path("type").asText() == "ACCOUNT" && it.path("id").asInt() == account.id() },
                "Account found"
            )
            assertNotNull(
                targets.find { it.path("type").asText() == "GROUP" && it.path("id").asInt() == group.id() },
                "Account group found"
            )
        }

        // Account only
        run(query, mapOf("token" to account.name)) { data ->
            val targets = data.path("permissionTargets")
            assertNotNull(
                targets.find { it.path("type").asText() == "ACCOUNT" && it.path("id").asInt() == account.id() },
                "Account found"
            )
            assertNull(
                targets.find { it.path("type").asText() == "GROUP" && it.path("id").asInt() == group.id() },
                "Account group not found"
            )
        }

        // Group only
        run(query, mapOf("token" to group.name)) { data ->
            val targets = data.path("permissionTargets")
            assertNull(
                targets.find { it.path("type").asText() == "ACCOUNT" && it.path("id").asInt() == account.id() },
                "Account not found"
            )
            assertNotNull(
                targets.find { it.path("type").asText() == "GROUP" && it.path("id").asInt() == group.id() },
                "Account group found"
            )
        }
    }

}