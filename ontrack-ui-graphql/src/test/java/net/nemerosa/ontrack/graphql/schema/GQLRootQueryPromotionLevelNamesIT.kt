package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GQLRootQueryPromotionLevelNamesIT: AbstractQLKTITSupport() {

    @Test
    fun `Getting filtered names of promotion levels across projects`() {
        withNoGrantViewToAll {
            val prefix = uid("pl_")

            val pla = project<PromotionLevel> {
                branch<PromotionLevel> {
                    promotionLevel(name = uid(prefix))
                }
            }

            val plb = project<PromotionLevel> {
                branch<PromotionLevel> {
                    promotionLevel(name = uid(prefix))
                }
            }

            asAdmin {

                // Gets all of them
                run(
                    """{
                        promotionLevelNames
                    }"""
                ) { data ->
                    val names = data["promotionLevelNames"].map { it.asText() }
                    assertTrue(names.contains(pla.name), "Contains the first PL")
                    assertTrue(names.contains(plb.name), "Contains the second PL")
                }

                // Restriction on token
                run(
                    """{
                    promotionLevelNames(token: "$prefix")
                }"""
                ) { data ->
                    val names = data["promotionLevelNames"].map { it.asText() }
                    assertTrue(names.contains(pla.name), "Contains the first PL")
                    assertTrue(names.contains(plb.name), "Contains the second PL")
                }

                // Restriction on PL name
                run(
                    """{
                    promotionLevelNames(token: "${pla.name}")
                }"""
                ) { data ->
                    val names = data["promotionLevelNames"].map { it.asText() }
                    assertEquals(listOf(pla.name), names)
                }

            }

            // Restriction on access rights
            asUser().withView(pla).execute {
                run(
                    """{
                    promotionLevelNames
                }"""
                ) { data ->
                    val names = data["promotionLevelNames"].map { it.asText() }
                    assertTrue(names.contains(pla.name), "Contains the first PL")
                    assertFalse(names.contains(plb.name), "No access to the second PL")
                }
            }
        }
    }

}