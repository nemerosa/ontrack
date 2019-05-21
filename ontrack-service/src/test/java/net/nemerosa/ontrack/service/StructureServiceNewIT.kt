package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.PromotionLevelCreate
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * This class supersedes [StructureServiceIT], which cannot be replaced as yet.
 */
class StructureServiceNewIT : AbstractDSLTestSupport() {

    @Test
    fun `New promotion level based on a predefined promotion level with description and image`() {
        // Unique promotion name
        val promotionName = uid("P")
        // Predefined promotion level
        predefinedPromotionLevel(promotionName, "My predefined description", image = true)
        // Creating a promotion level
        project {
            branch {
                asUser().with(this, PromotionLevelCreate::class.java).call {
                    val pl = structureService.newPromotionLevel(
                            PromotionLevel.of(
                                    this,
                                    NameDescription.nd(promotionName, "")
                            )
                    )
                    // Description must be aligned
                    assertEquals("My predefined description", pl.description)
                    // An image must have been set
                    assertTrue(pl.image, "An image must have been set")
                }
            }
        }
    }

}