package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AutoValidationStampPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the auto validation stamp property on a project by ID and then removing it`() {
        asAdmin {
            project {
                run("""
                    mutation {
                        setProjectAutoValidationStampPropertyById(input: {
                            id: $id,
                            isAutoCreate: true
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "setProjectAutoValidationStampPropertyById")
                    assertNotNull(getProperty(this, AutoValidationStampPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                        assertEquals(false, property.isAutoCreateIfNotPredefined)
                    }
                }
                run("""
                    mutation {
                        deleteProjectAutoValidationStampPropertyById(input: {
                            id: $id
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "deleteProjectAutoValidationStampPropertyById")
                    assertNull(getProperty(this, AutoValidationStampPropertyType::class.java))
                }
            }
        }
    }

    @Test
    fun `Setting the auto validation stamp property on a project by name and then removing it`() {
        asAdmin {
            project {
                run("""
                    mutation {
                        setProjectAutoValidationStampProperty(input: {
                            project: "${project.name}",
                            isAutoCreate: true,
                            isAutoCreateIfNotPredefined: true
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "setProjectAutoValidationStampProperty")
                    assertNotNull(getProperty(this, AutoValidationStampPropertyType::class.java)) { property ->
                        assertEquals(true, property.isAutoCreate)
                        assertEquals(true, property.isAutoCreateIfNotPredefined)
                    }
                }
                run("""
                    mutation {
                        deleteProjectAutoValidationStampProperty(input: {
                            project: "${project.name}"
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """).let { data ->
                    assertNoUserError(data, "deleteProjectAutoValidationStampProperty")
                    assertNull(getProperty(this, AutoValidationStampPropertyType::class.java))
                }
            }
        }
    }

}