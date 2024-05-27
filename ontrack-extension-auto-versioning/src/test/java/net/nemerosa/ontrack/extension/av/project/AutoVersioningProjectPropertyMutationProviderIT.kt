package net.nemerosa.ontrack.extension.av.project

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AutoVersioningProjectPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting auto versioning by ID`() {
        asAdmin {
            project {
                run(
                    """
                mutation {
                    setProjectAutoVersioningProjectPropertyById(input: {
                        id: $id,
                        branchIncludes: ["release-2024.*", "main"],
                        branchExcludes: ["release-202401"],
                        lastActivityDate: "2024-04-03T03:04:00",
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """
                ).let { data ->
                    assertNoUserError(data, "setProjectAutoVersioningProjectPropertyById")

                    assertNotNull(getProperty(this, AutoVersioningProjectPropertyType::class.java)) { property ->
                        assertEquals(listOf("release-2024.*", "main"), property.branchIncludes)
                        assertEquals(listOf("release-202401"), property.branchExcludes)
                        assertEquals(
                            LocalDateTime.of(2024, Month.APRIL, 3, 3, 4, 0),
                            property.lastActivityDate,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Setting auto versioning by name`() {
        asAdmin {
            project {
                run(
                    """
                        mutation {
                            setProjectAutoVersioningProjectProperty(input: {
                                project: "$name",
                                branchIncludes: ["release-2024.*", "main"],
                                branchExcludes: ["release-202401"],
                                lastActivityDate: "2024-04-03T03:04:00",
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                ).let { data ->
                    assertNoUserError(data, "setProjectAutoVersioningProjectProperty")

                    assertNotNull(getProperty(this, AutoVersioningProjectPropertyType::class.java)) { property ->
                        assertEquals(listOf("release-2024.*", "main"), property.branchIncludes)
                        assertEquals(listOf("release-202401"), property.branchExcludes)
                        assertEquals(
                            LocalDateTime.of(2024, Month.APRIL, 3, 3, 4, 0),
                            property.lastActivityDate,
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Deleting auto versioning by ID`() {
        asAdmin {
            project {
                setProperty(
                    this, AutoVersioningProjectPropertyType::class.java, AutoVersioningProjectProperty(
                        branchIncludes = listOf("release-2024.*", "main"),
                        branchExcludes = listOf("release-202401"),
                        lastActivityDate = null,
                    )
                )
                run(
                    """
                        mutation {
                            deleteProjectAutoVersioningProjectPropertyById(input: {
                                id: $id,
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                ) { data ->
                    assertNoUserError(data, "deleteProjectAutoVersioningProjectPropertyById")

                    assertNull(
                        getProperty(this, AutoVersioningProjectPropertyType::class.java),
                        "Property has been deleted"
                    )
                }
            }
        }
    }

    @Test
    fun `Deleting auto versioning by name`() {
        asAdmin {
            project {
                setProperty(
                    this, AutoVersioningProjectPropertyType::class.java, AutoVersioningProjectProperty(
                        branchIncludes = listOf("release-2024.*", "main"),
                        branchExcludes = listOf("release-202401"),
                        lastActivityDate = null,
                    )
                )
                run(
                    """
                        mutation {
                            deleteProjectAutoVersioningProjectProperty(input: {
                                project: "$name",
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                ) { data ->
                    assertNoUserError(data, "deleteProjectAutoVersioningProjectProperty")

                    assertNull(
                        getProperty(this, AutoVersioningProjectPropertyType::class.java),
                        "Property has been deleted"
                    )
                }
            }
        }
    }
}
