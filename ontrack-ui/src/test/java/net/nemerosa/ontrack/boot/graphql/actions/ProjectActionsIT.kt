package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals

class ProjectActionsIT : AbstractQLKTITSupport() {

    @Test
    fun `Project actions when full control`() {
        asAdmin {
            project {
                val data = run("""{
                     projects(id: $id) {
                        actions {
                            updateProject {
                                links {
                                    form {
                                        method
                                        uri
                                    }
                                }
                                mutation
                            }
                            deleteProject {
                                mutation
                            }
                            disableProject {
                                mutation
                            }
                            enableProject {
                                mutation
                            }
                        }
                     }
                }""")
                val actions = data["projects"][0]["actions"]
                assertEquals(
                        mapOf(
                                "updateProject" to mapOf(
                                        "links" to mapOf(
                                                "form" to mapOf(
                                                        "method" to "GET",
                                                        "uri" to "uri"
                                                )
                                        ),
                                        "mutation" to "updateProject"
                                ),
                                "deleteProject" to mapOf(
                                        "mutation" to "deleteProject"
                                ),
                                "disableProject" to mapOf(
                                        "mutation" to "disableProject"
                                ),
                                "enableProject" to mapOf(
                                        "mutation" to "enableProject"
                                )
                        ).asJson(),
                        actions
                )
            }
        }
    }

}