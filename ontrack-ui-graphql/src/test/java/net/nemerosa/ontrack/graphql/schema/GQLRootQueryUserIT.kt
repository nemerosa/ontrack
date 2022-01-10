package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITJUnit4Support
import org.junit.Test
import kotlin.test.assertEquals

class GQLRootQueryUserIT : AbstractQLKTITJUnit4Support() {

    @Test
    fun `Getting the current user`() {
        val account = doCreateAccount()
        asFixedAccount(account) {
            val data = run("""{
                user {
                    account {
                        id
                    }
                }
            }""")
            assertEquals(
                    account.id(),
                    data["user"]["account"]["id"].asInt()
            )
        }
    }

}