package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedPromotionLevelNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.PromotionLevelEdit
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired

import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.*

class PredefinedPromotionLevelServiceIT: AbstractServiceTestSupport() {

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
            val pl = service.findPredefinedPromotionLevelByName(pl1.name).getOrNull()
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
            val pl = service.findPredefinedPromotionLevelByName(pl1.name).getOrNull()
            assertNotNull(pl) {
                assertEquals("My new description", it.description)
                assertTrue(it.isImage)
            }
        }
    }

}