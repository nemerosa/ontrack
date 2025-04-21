package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.PromotionLevelEdit
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

@AsAdminTest
class PredefinedPromotionLevelServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: PredefinedPromotionLevelService

    @Test
    fun `Predefined promotion level creation`() {
        val name = uid("PVS")
        val predefinedPromotionLevel = asUser().with(GlobalSettings::class.java).call {
            service.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                    NameDescription.nd(
                        name,
                        "Predefined $name"
                    )
                )
            )
        }
        assertNotNull(predefinedPromotionLevel) {
            assertTrue(it.id.isSet)
        }
    }

    @Test
    fun `Predefined promotion level name already exists`() {
        val name = uid("PVS")
        // Once --> OK
        asUser().with(GlobalSettings::class.java).call {
            service.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                    NameDescription.nd(
                        name,
                        "Predefined $name"
                    )
                )
            )
        }
        // Twice --> NOK
        asUser().with(GlobalSettings::class.java).call {
            assertFailsWith<PredefinedPromotionLevelNameAlreadyDefinedException> {
                service.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(
                        NameDescription.nd(
                            name,
                            "Predefined other $name"
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Bulk update of promotion levels creates a predefined promotion level`() {
        // Creates three promotion levels, two with the same name, one with a different name
        val pl1Name = uid("PL")
        val pl2Name = uid("PL")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val branch3 = doCreateBranch()
        val pl1 = doCreatePromotionLevel(branch1, NameDescription.nd(pl1Name, ""))
        val pl2 = doCreatePromotionLevel(branch2, NameDescription.nd(pl1Name, ""))
        val pl3 = doCreatePromotionLevel(branch3, NameDescription.nd(pl2Name, ""))

        // Updates the PL1 description and image
        asUser().withProjectFunction(pl1, PromotionLevelEdit::class.java).call {
            structureService.savePromotionLevel(
                pl1.withDescription("My new description")
            )
            structureService.setPromotionLevelImage(
                pl1.id,
                Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings::class.java).call {
            structureService.bulkUpdatePromotionLevels(pl1.id)
        }

        // Checks the promotion level with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getPromotionLevel(pl2.id).description)
            assertTrue(structureService.getPromotionLevel(pl2.id).isImage)
        }

        // Checks the promotion level with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getPromotionLevel(pl3.id).description)
            assertFalse(structureService.getPromotionLevel(pl3.id).isImage)
        }

        // Checks the predefined promotion level has been created
        asAdmin().call {
            val pl = service.findPredefinedPromotionLevelByName(pl1.name)
            assertNotNull(pl) {
                assertEquals("My new description", it.description)
                assertTrue(it.isImage)
            }
        }
    }

    @Test
    fun `Bulk update of promotion levels updates a predefined promotion level`() {
        // Creates three promotion levels, two with the same name, one with a different name
        val pl1Name = uid("PL")
        val pl2Name = uid("PL")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val branch3 = doCreateBranch()
        val pl1 = doCreatePromotionLevel(branch1, NameDescription.nd(pl1Name, ""))
        val pl2 = doCreatePromotionLevel(branch2, NameDescription.nd(pl1Name, ""))
        val pl3 = doCreatePromotionLevel(branch3, NameDescription.nd(pl2Name, ""))
        // Predefined promotion level
        val ppl = asAdmin().call {
            service.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(NameDescription.nd(pl1Name, ""))
            )
        }

        // Updates the PL1 description and image
        asUser().withProjectFunction(pl1, PromotionLevelEdit::class.java).call {
            structureService.savePromotionLevel(
                pl1.withDescription("My new description")
            )
            structureService.setPromotionLevelImage(
                pl1.id,
                Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"))
            )
        }

        // Bulk update
        asUser().with(GlobalSettings::class.java).call {
            structureService.bulkUpdatePromotionLevels(pl1.id)
        }

        // Checks the promotion level with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getPromotionLevel(pl2.id).description)
            assertTrue(structureService.getPromotionLevel(pl2.id).isImage)
        }

        // Checks the promotion level with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getPromotionLevel(pl3.id).description)
            assertFalse(structureService.getPromotionLevel(pl3.id).isImage)
        }

        // Checks the predefined promotion level has been created
        asAdmin().call {
            val pl = service.findPredefinedPromotionLevelByName(pl1.name)
            assertNotNull(pl) {
                assertEquals("My new description", it.description)
                assertTrue(it.isImage)
            }
        }
    }

    @Test
    fun `Updating a promotion level must keep the predefined promotion level description`() {
        asAdmin {
            val plName = uid("pl-")
            val ppl = service.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                    NameDescription.nd(plName, "Description at predefined level")
                )
            )

            project {
                branch {
                    // Creating a promotion level
                    val pl1 = promotionLevel(name = plName)
                    // Predefined description must have been set
                    assertEquals(ppl.description, pl1.description)

                    // Updating the promotion level without a description
                    structureService.savePromotionLevel(
                        pl1.withDescription("")
                    )
                    val pl2 = structureService.getPromotionLevel(pl1.id)
                    // Predefined description must have been kept
                    assertEquals(ppl.description, pl2.description)
                }
            }
        }
    }

    @Test
    fun `Reordering of predefined promotion levels`() {
        asAdmin {
            // Deletes all previous entries
            predefinedPromotionLevelService.predefinedPromotionLevels.forEach {
                predefinedPromotionLevelService.deletePredefinedPromotionLevel(it.id)
            }
            // Creating some entries
            val entries = (1..10).map {
                val entry = PredefinedPromotionLevel.of(
                    NameDescription.nd(
                        name = "PL_$it",
                        description = "Promotion level $it",
                    )
                )
                predefinedPromotionLevelService.newPredefinedPromotionLevel(entry)
            }

            // Dropping a middle entry to the first one
            predefinedPromotionLevelService.reorderPromotionLevels(
                activeId = entries[5].id(),
                overId = entries[0].id(),
            )

            // Reloading the entries
            val reorderedEntries = predefinedPromotionLevelService.predefinedPromotionLevels

            assertEquals(
                listOf(
                    entries[5].id(),
                    entries[0].id(),
                    entries[1].id(),
                    entries[2].id(),
                    entries[3].id(),
                    entries[4].id(),
                    entries[6].id(),
                    entries[7].id(),
                    entries[8].id(),
                    entries[9].id(),
                ),
                reorderedEntries.map { it.id() }
            )
        }
    }

}