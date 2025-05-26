package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.AccountLoginService
import net.nemerosa.ontrack.model.security.GroupMappingService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLTypeUserIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var groupMappingService: GroupMappingService

    @Autowired
    private lateinit var accountLoginService: AccountLoginService

    @Test
    @AsAdminTest
    fun `List of groups for the current user`() {
        val assignedGroup = doCreateAccountGroup()
        val account = doCreateAccount(assignedGroup)
        val mappedGroup = doCreateAccountGroup()
        val idpGroup = uid("/idp-")

        // Registering the IdP group for the account
        accountLoginService.login(account.email, account.fullName, listOf(idpGroup))
        // Mapping the IdP group to the Yontrack group
        groupMappingService.mapGroup(idpGroup, mappedGroup)

        asFixedAccount(account) {
            run(
                """
                    {
                        user {
                            account {
                                email
                            }
                            assignedGroups {
                                name
                            }
                            mappedGroups {
                                name
                            }
                            idpGroups
                        }
                    }
                """
            ) { data ->
                assertEquals(
                    mapOf(
                        "user" to mapOf(
                            "account" to mapOf(
                                "email" to account.email
                            ),
                            "assignedGroups" to listOf(
                                mapOf("name" to assignedGroup.name)
                            ),
                            "mappedGroups" to listOf(
                                mapOf("name" to mappedGroup.name)
                            ),
                            "idpGroups" to listOf(idpGroup),
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

}