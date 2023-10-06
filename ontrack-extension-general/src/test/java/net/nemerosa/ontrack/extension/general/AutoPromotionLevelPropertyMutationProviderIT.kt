package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertJsonNotNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AutoPromotionLevelPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Creating a promotion run with an existing promotion level`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        run(
                            """
                            mutation {
                                createPromotionRunById(input: {
                                    buildId: $id,
                                    promotion: "${pl.name}",
                                    description: null,
                                }) {
                                    promotionRun {
                                        id
                                    }
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }
                        """
                        ) { data ->
                            checkGraphQLUserErrors(data, "createPromotionRunById") {
                                assertJsonNotNull(it.path("promotionRun").path("id"))
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run with an existing predefined promotion level without auto creation fails`() {
        asAdmin {
            project {
                branch {
                    val ppl = predefinedPromotionLevel()
                    build {
                        run(
                            """
                            mutation {
                                createPromotionRunById(input: {
                                    buildId: $id,
                                    promotion: "${ppl.name}",
                                    description: null,
                                }) {
                                    promotionRun {
                                        id
                                    }
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }
                        """
                        ) { data ->
                            assertUserError(data, "createPromotionRunById", "Promotion level not found: ${project.name}/${branch.name}/${ppl.name}")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run with an existing predefined promotion level with auto creation`() {
        asAdmin {
            project {
                autoPromotionLevelProperty(this, autoCreate = true)
                branch {
                    val ppl = predefinedPromotionLevel()
                    build {
                        run(
                            """
                            mutation {
                                createPromotionRunById(input: {
                                    buildId: $id,
                                    promotion: "${ppl.name}",
                                    description: null,
                                }) {
                                    promotionRun {
                                        id
                                    }
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }
                        """
                        ) { data ->
                            checkGraphQLUserErrors(data, "createPromotionRunById") {
                                assertJsonNotNull(it.path("promotionRun").path("id"))
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a promotion run without an existing predefined promotion level with auto creation fails`() {
        asAdmin {
            project {
                autoPromotionLevelProperty(this, autoCreate = true)
                branch {
                    build {
                        val plName = TestUtils.uid("pl_")
                        run(
                            """
                            mutation {
                                createPromotionRunById(input: {
                                    buildId: $id,
                                    promotion: "$plName",
                                    description: null,
                                }) {
                                    promotionRun {
                                        id
                                    }
                                    errors {
                                        message
                                        exception
                                    }
                                }
                            }
                        """
                        ) { data ->
                            assertUserError(data, "createPromotionRunById", "Promotion level not found: ${project.name}/${branch.name}/$plName")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the auto promotion level property on a project by ID and then removing it`() {
        asAdmin {
            project {
                run(
                    """
                    mutation {
                        setProjectAutoPromotionLevelPropertyById(input: {
                            id: $id,
                            isAutoCreate: true
                        }) {
                            errors {
                                message
                                        exception
                            }
                        }
                    }
                """
                ).let { data ->
                    assertNoUserError(data, "setProjectAutoPromotionLevelPropertyById")
                    assertNotNull(getProperty(this, AutoPromotionLevelPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                    }
                }
                run(
                    """
                    mutation {
                        deleteProjectAutoPromotionLevelPropertyById(input: {
                            id: $id
                        }) {
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """
                ).let { data ->
                    assertNoUserError(data, "deleteProjectAutoPromotionLevelPropertyById")
                    assertNull(getProperty(this, AutoPromotionLevelPropertyType::class.java))
                }
            }
        }
    }

    @Test
    fun `Setting the auto promotion level property on a project by name and then removing it`() {
        asAdmin {
            project {
                run(
                    """
                    mutation {
                        setProjectAutoPromotionLevelProperty(input: {
                            project: "${project.name}",
                            isAutoCreate: true
                        }) {
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """
                ).let { data ->
                    assertNoUserError(data, "setProjectAutoPromotionLevelProperty")
                    assertNotNull(getProperty(this, AutoPromotionLevelPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                    }
                }
                run(
                    """
                    mutation {
                        deleteProjectAutoPromotionLevelProperty(input: {
                            project: "${project.name}"
                        }) {
                            errors {
                                message
                                exception
                            }
                        }
                    }
                """
                ).let { data ->
                    assertNoUserError(data, "deleteProjectAutoPromotionLevelProperty")
                    assertNull(getProperty(this, AutoPromotionLevelPropertyType::class.java))
                }
            }
        }
    }

}