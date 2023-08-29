package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.test.TestUtils.resourceBytes
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class PredefinedPromotionLevelsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var testSCMExtension: TestSCMExtension

    @Autowired
    private lateinit var predefinedPromotionLevelsAdminContext: PredefinedPromotionLevelsAdminContext

    @Test
    fun `Rendering existing predefined promotion levels`() {
        asAdmin {
            // Deleting all existing elements
            predefinedPromotionLevelService.predefinedPromotionLevels.forEach {
                predefinedPromotionLevelService.deletePredefinedPromotionLevel(it.id)
            }
            // Creating a couple of elements
            (1..3).forEach {
                predefinedPromotionLevelService.newPredefinedPromotionLevel(
                    PredefinedPromotionLevel.of(NameDescription.nd("PL_$it", "PPL $it"))
                )
            }
            // Rendering the Casc node
            val node = predefinedPromotionLevelsAdminContext.render()
            assertEquals(
                mapOf(
                    "replace" to false,
                    "list" to (1..3).map {
                        mapOf(
                            "name" to "PL_$it",
                            "description" to "PPL $it",
                            "image" to null,
                        )
                    }
                ).asJson(),
                node,
            )
        }
    }

    @Test
    fun `Adding a predefined promotion level`() {
        val name = uid("pl_")
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-promotion-levels:
                        replace: false
                        list:
                            - name: $name
                              description: Sample predefined test called $name
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name).getOrNull(),
            "$name predefined PL has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
        }
    }

    @Test
    fun `Replacing the predefined promotion levels`() {
        val existing = uid("pl_")
        val name = uid("pl_")
        // Creates an existing PPL
        asAdmin {
            predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(NameDescription.nd(existing, "Existing"))
            )
        }
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-promotion-levels:
                        replace: true
                        list:
                            - name: $name
                              description: Sample predefined test called $name
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name).getOrNull(),
            "$name predefined PL has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
        }
        // Checks that the existing PPL is not present any longer
        assertNull(
            predefinedPromotionLevelService.findPredefinedPromotionLevelByName(existing).getOrNull(),
            "Existing PPL have been removed"
        )
    }

    @Test
    fun `Not replacing existing images`() {
        val existing = uid("pl_")
        // Creates an existing PPL
        asAdmin {
            val ppl = predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(NameDescription.nd(existing, "Existing"))
            )
            predefinedPromotionLevelService.setPredefinedPromotionLevelImage(
                ppl.id,
                Document(
                    type = "image/png",
                    content = resourceBytes("/casc/bronze.png")
                )
            )
        }
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-promotion-levels:
                        replace: false
                        list:
                            - name: $existing
                              description: Sample predefined test called $existing
        """.trimIndent()
        )
        // Checks that the new predefined element is still there
        assertNotNull(
            predefinedPromotionLevelService.findPredefinedPromotionLevelByName(existing).getOrNull(),
            "$existing predefined PL has been kept"
        ) {
            assertEquals("Sample predefined test called $existing", it.description)
            val storedImage = predefinedPromotionLevelService.getPredefinedPromotionLevelImage(it.id)
            assertFalse(storedImage.isEmpty, "Image has been saved")
            val expectedImage = resourceBytes("/casc/bronze.png")
            assertContentEquals(expectedImage, storedImage.content, "Expected image content")
        }
    }

    @Test
    fun `Predefined promotion levels using an image`() {
        val name = uid("pl_")
        // Creates fake test scm config
        val config = uid("scm_")
        // Registering the image
        testSCMExtension.registerConfigForTestSCM(config) {
            withBinaryFile("casc/bronze.png", branch = null /* default branch */) {
                resourceBytes("/casc/bronze.png")
            }
        }
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-promotion-levels:
                        replace: false
                        list:
                            - name: $name
                              description: Sample predefined test called $name
                              image: scm://test/$config/casc/bronze.png
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedPromotionLevelService.findPredefinedPromotionLevelByName(name).getOrNull(),
            "$name predefined PL has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
            val storedImage = predefinedPromotionLevelService.getPredefinedPromotionLevelImage(it.id)
            assertFalse(storedImage.isEmpty, "Image has been saved")
            val expectedImage = resourceBytes("/casc/bronze.png")
            assertContentEquals(expectedImage, storedImage.content, "Expected image content")
        }
    }

}