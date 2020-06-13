package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals

class RootUserActionsIT: AbstractQLKTITSupport() {

    @Test
    fun `Creation of a project being allowed`() {
        asAdmin {
            val data = run ("""{
                user {
                    actions {
                        createProject {
                            description
                            links {
                                form {
                                    method
                                    uri
                                }
                            }
                            mutation
                        }
                    }
                }
            }""")
            val actions = data["user"]["actions"]
            val create = actions["createProject"]
            assertEquals("createProject", create["mutation"].asText())
            val links = create["links"]
            val form = links["form"]
            assertEquals("GET", form["method"].asText())
            assertEquals("", form["uri"].asText())
        }
    }

}