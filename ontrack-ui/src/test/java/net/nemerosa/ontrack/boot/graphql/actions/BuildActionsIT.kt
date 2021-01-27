package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ValidationRunController
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BuildActionsIT : AbstractQLKTITSupport() {

    @Test
    fun `Build actions when full control`() {
        asAdmin {
            project {
                branch {
                    build {
                        val data = run(
                            """{
                             builds(id: $id) {
                                actions {
                                    createValidationRunForBuildById {
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
                                }
                             }
                        }"""
                        )
                        val actions = data["builds"][0]["actions"]
                        assertEquals(
                            mapOf(
                                "createValidationRunForBuildById" to mapOf(
                                    "description" to "Creating a validation run for this build",
                                    "links" to mapOf(
                                        "form" to mapOf(
                                            "description" to "Creating a validation run for this build",
                                            "method" to "GET",
                                            "uri" to "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunController#newValidationRunForm:$id"
                                        )
                                    ),
                                    "mutation" to "createValidationRunForBuildById"
                                )
                            ).asJson(),
                            actions
                        )
                    }
                }
            }
        }
    }

}