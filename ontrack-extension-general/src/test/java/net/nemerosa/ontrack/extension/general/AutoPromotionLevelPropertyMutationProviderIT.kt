package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AutoPromotionLevelPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the auto promotion level property on a project by ID and then removing it`() {
        asAdmin {
            project {
                run("""
                    mutation {
                        setProjectAutoPromotionLevelPropertyById(input: {
                            id: $id,
                            isAutoCreate: true
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "setProjectAutoPromotionLevelPropertyById")
                    assertNotNull(getProperty(this, AutoPromotionLevelPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                    }
                }
                run("""
                    mutation {
                        deleteProjectAutoPromotionLevelPropertyById(input: {
                            id: $id
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
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
                run("""
                    mutation {
                        setProjectAutoPromotionLevelProperty(input: {
                            project: "${project.name}",
                            isAutoCreate: true
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "setProjectAutoPromotionLevelProperty")
                    assertNotNull(getProperty(this, AutoPromotionLevelPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                    }
                }
                run("""
                    mutation {
                        deleteProjectAutoPromotionLevelProperty(input: {
                            project: "${project.name}"
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "deleteProjectAutoPromotionLevelProperty")
                    assertNull(getProperty(this, AutoPromotionLevelPropertyType::class.java))
                }
            }
        }
    }

}