package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationRunQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Creating a validation run with missing status`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    val data = run("""
                        mutation CreateValidationRun {
                            createValidationRun(input: {
                                project: "${project.name}",
                                branch: "${branch.name}",
                                build: "$name",
                                validationStamp: "${vs.name}"
                            }) {
                                validationRun {
                                    validationRunStatuses {
                                        statusID {
                                            id
                                        }
                                    }
                                }
                                errors {
                                    message
                                    exception
                                }
                            }
                        }
                    """)
                    // Checks the errors
                    val error = data["createValidationRun"]["errors"][0]
                    assertEquals("Validation Run Status is required because no data is provided.", error["message"].asText())
                    assertEquals("net.nemerosa.ontrack.model.exceptions.ValidationRunDataStatusRequiredBecauseNoDataException", error["exception"].asText())
                    assertTrue(data["createValidationRun"]["validationRun"].isNullOrNullNode(), "Validation run not returned")
                }
            }
        }
    }

    @Test
    fun `Creating a PASSED validation run`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    val data = run("""
                        mutation CreateValidationRun {
                            createValidationRun(input: {
                                project: "${project.name}",
                                branch: "${branch.name}",
                                build: "$name",
                                validationStamp: "${vs.name}",
                                validationRunStatus: "PASSED"
                            }) {
                                validationRun {
                                    validationRunStatuses {
                                        statusID {
                                            id
                                        }
                                    }
                                }
                            }
                        }
                    """)
                    assertEquals(
                        "PASSED",
                        data.path("createValidationRun").path("validationRun").path("validationRunStatuses").path(0).path("statusID").path("id").asText()
                    )
                }
            }
        }
    }

    @Test
    fun `Validation run status with links in description`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    val run = validate(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_FAILED,
                            description = "Some text"
                    ).apply {
                        validationStatus(ValidationRunStatusID.STATUS_DEFECTIVE, "See https://issues/browser/ONT-1234")
                    }
                    val data = run("""{
                        validationRuns(id: ${run.id}) {
                            validationRunStatuses {
                                annotatedDescription
                            }
                        }
                    }""")
                    val descriptions = data["validationRuns"][0]["validationRunStatuses"].map { it["annotatedDescription"].asText() }
                    assertEquals(
                            listOf(
                                    """See <a href="https://issues/browser/ONT-1234" target="_blank">https://issues/browser/ONT-1234</a>""",
                                    "Some text"
                            ),
                            descriptions
                    )
                }
            }
        }
    }

    @Test
    fun `Validation run statuses for a validation run`() {
        project {
            branch {
                val vs = validationStamp("VS")
                build("1") {
                    val run = validate(
                            validationStamp = vs,
                            validationRunStatusID = ValidationRunStatusID.STATUS_FAILED,
                            description = "Validation failed"
                    ).apply {
                        validationStatus(ValidationRunStatusID.STATUS_INVESTIGATING, "Investigating")
                        validationStatus(ValidationRunStatusID.STATUS_EXPLAINED, "Explained")
                    }
                    val data = run("""{
                        validationRuns(id: ${run.id}) {
                            validationRunStatuses {
                                statusID {
                                    id
                                }
                                description
                            }
                        }
                    }""")
                    val validationRunStatuses = data["validationRuns"][0]["validationRunStatuses"]
                    assertEquals(
                            listOf("EXPLAINED", "INVESTIGATING", "FAILED"),
                            validationRunStatuses.map { it["statusID"]["id"].asText() }
                    )
                    assertEquals(
                            listOf("Explained", "Investigating", "Validation failed"),
                            validationRunStatuses.map { it["description"].asText() }
                    )
                }
            }
        }
    }

    @Test
    fun `Validation run reference to build and validation stamp`() {
        project {
            branch {
                val vs = validationStamp("VS")
                build("1") {
                    val run = validate(vs, ValidationRunStatusID.STATUS_PASSED)
                    val data = run("""{
                        validationRuns(id: ${run.id}) {
                            build {
                                id
                            }
                            validationStamp {
                                id
                                branch {
                                    id
                                    project {
                                        id
                                    }
                                }
                            }
                        }
                    }""")
                    val v = data["validationRuns"].first()
                    assertEquals(this.id(), v["build"]["id"].asInt())
                    assertEquals(vs.id(), v["validationStamp"]["id"].asInt())
                    assertEquals(this@branch.id(), v["validationStamp"]["branch"]["id"].asInt())
                    assertEquals(this@project.id(), v["validationStamp"]["branch"]["project"]["id"].asInt())
                }
            }
        }
    }

}
