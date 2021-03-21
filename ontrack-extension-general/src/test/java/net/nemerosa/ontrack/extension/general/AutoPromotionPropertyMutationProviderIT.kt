package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AutoPromotionPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting auto promotion by ID`() {
        asAdmin {
            project {
                branch {
                    val vs1 = validationStamp()
                    val vs2 = validationStamp()
                    val other = promotionLevel()
                    val pl = promotionLevel()
                    run("""
                        mutation {
                            setPromotionLevelAutoPromotionPropertyById(input: {
                                id: ${pl.id},
                                validationStamps: ["${vs1.name}", "${vs2.name}"],
                                promotionLevels: ["${other.name}"]
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        assertNoUserError(data, "setPromotionLevelAutoPromotionPropertyById")

                        assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
                            assertEquals("", property.include)
                            assertEquals("", property.exclude)
                            assertEquals(
                                setOf(vs1.name, vs2.name),
                                property.validationStamps.map { it.name }.toSet()
                            )
                            assertEquals(
                                setOf(other.name),
                                property.promotionLevels.map { it.name }.toSet()
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting auto promotion by name`() {
        asAdmin {
            project {
                branch {
                    val vs1 = validationStamp()
                    val vs2 = validationStamp()
                    val other = promotionLevel()
                    val pl = promotionLevel()
                    run("""
                        mutation {
                            setPromotionLevelAutoPromotionProperty(input: {
                                project: "${pl.project.name}",
                                branch: "${pl.branch.name}",
                                promotion: "${pl.name}",
                                validationStamps: ["${vs1.name}", "${vs2.name}"],
                                promotionLevels: ["${other.name}"]
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        assertNoUserError(data, "setPromotionLevelAutoPromotionProperty")

                        assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
                            assertEquals("", property.include)
                            assertEquals("", property.exclude)
                            assertEquals(
                                setOf(vs1.name, vs2.name),
                                property.validationStamps.map { it.name }.toSet()
                            )
                            assertEquals(
                                setOf(other.name),
                                property.promotionLevels.map { it.name }.toSet()
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting auto promotion with exclude and include by name`() {
        asAdmin {
            project {
                branch {
                    val other = promotionLevel()
                    val pl = promotionLevel()
                    run("""
                        mutation {
                            setPromotionLevelAutoPromotionProperty(input: {
                                project: "${pl.project.name}",
                                branch: "${pl.branch.name}",
                                promotion: "${pl.name}",
                                promotionLevels: ["${other.name}"],
                                include: "acceptance-.*",
                                exclude: "acceptance-long-.*"
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        assertNoUserError(data, "setPromotionLevelAutoPromotionProperty")

                        assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
                            assertEquals("acceptance-.*", property.include)
                            assertEquals("acceptance-long-.*", property.exclude)
                            assertEquals(
                                emptySet(),
                                property.validationStamps.map { it.name }.toSet()
                            )
                            assertEquals(
                                setOf(other.name),
                                property.promotionLevels.map { it.name }.toSet()
                            )
                        }
                    }
                }
            }
        }
    }

}