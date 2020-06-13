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
                                description
                                links {
                                    form {
                                        description
                                        method
                                        uri
                                    }
                                }
                                mutation
                            }
                            deleteProject {
                                description
                                mutation
                            }
                            disableProject {
                                description
                                mutation
                            }
                            enableProject {
                                description
                                mutation
                            }
                        }
                     }
                }""")
                val actions = data["projects"][0]["actions"]
                assertEquals(
                        mapOf(
                                "updateProject" to mapOf(
                                        "description" to "Updating the project",
                                        "links" to mapOf(
                                                "form" to mapOf(
                                                        "description" to "Update form",
                                                        "method" to "GET",
                                                        "uri" to "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProjectForm:$id"
                                                )
                                        ),
                                        "mutation" to "updateProject"
                                ),
                                "deleteProject" to mapOf(
                                        "description" to "Deleting the project",
                                        "mutation" to "deleteProject"
                                ),
                                "disableProject" to mapOf(
                                        "description" to "Disabling the project",
                                        "mutation" to "disableProject"
                                ),
                                "enableProject" to mapOf(
                                        "description" to "Enabling the project",
                                        "mutation" to "enableProject"
                                )
                        ).asJson(),
                        actions
                )
            }
        }
    }

}