package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
                                                        "description" to "Updating the project",
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

    @Test
    fun `Marking a project as favourite`() {
        asAdmin {
            project {
                // Project can be marked as a favourite
                run("""{
                     projects(id: $id) {
                        actions {
                            favouriteProject {
                                description
                                mutation
                            }
                            unfavouriteProject {
                                description
                                mutation
                            }
                        }
                     }
                }""").let { data ->
                    val actions = data["projects"][0]["actions"]
                    assertEquals(
                            mapOf(
                                    "favouriteProject" to mapOf(
                                            "description" to "Marks the project as a favourite",
                                            "mutation" to "favouriteProject"
                                    ),
                                    "unfavouriteProject" to mapOf(
                                            "description" to "Unmarks the project as a favourite",
                                            "mutation" to null
                                    )
                            ).asJson(),
                            actions
                    )
                }
                // Marking the project as favourite
                run("""mutation {
                     favouriteProject(input: {id: $id}) {
                        project {
                            favourite
                            actions {
                                favouriteProject {
                                    description
                                    mutation
                                }
                                unfavouriteProject {
                                    description
                                    mutation
                                }
                            }
                        }
                     }
                }""").let { data ->
                    val project = data["favouriteProject"]["project"]
                    assertTrue(project["favourite"].asBoolean())
                    val actions = project["actions"]
                    assertEquals(
                            mapOf(
                                    "favouriteProject" to mapOf(
                                            "description" to "Marks the project as a favourite",
                                            "mutation" to null
                                    ),
                                    "unfavouriteProject" to mapOf(
                                            "description" to "Unmarks the project as a favourite",
                                            "mutation" to "unfavouriteProject"
                                    )
                            ).asJson(),
                            actions
                    )
                }
                // Unmarking the project as favourite
                run("""mutation {
                     unfavouriteProject(input: {id: $id}) {
                        project {
                            favourite
                            actions {
                                favouriteProject {
                                    description
                                    mutation
                                }
                                unfavouriteProject {
                                    description
                                    mutation
                                }
                            }
                        }
                     }
                }""").let { data ->
                    val project = data["unfavouriteProject"]["project"]
                    assertFalse(project["favourite"].asBoolean())
                    val actions = project["actions"]
                    assertEquals(
                            mapOf(
                                    "favouriteProject" to mapOf(
                                            "description" to "Marks the project as a favourite",
                                            "mutation" to "favouriteProject"
                                    ),
                                    "unfavouriteProject" to mapOf(
                                            "description" to "Unmarks the project as a favourite",
                                            "mutation" to null
                                    )
                            ).asJson(),
                            actions
                    )
                }
            }
        }
    }

}