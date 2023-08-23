package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.scm.service.TestSCMExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.test.TestUtils.resourceBytes
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class PredefinedValidationStampsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var testSCMExtension: TestSCMExtension

    @Autowired
    private lateinit var predefinedValidationStampsAdminContext: PredefinedValidationStampsAdminContext

    @Test
    fun `Rendering existing predefined validation stamps`() {
        asAdmin {
            // Deleting all existing elements
            predefinedValidationStampService.predefinedValidationStamps.forEach {
                predefinedValidationStampService.deletePredefinedValidationStamp(it.id)
            }
            // Creating a couple of elements
            (1..3).forEach {
                predefinedValidationStampService.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(NameDescription.nd("VS_$it", "PVS $it"))
                )
            }
            // Rendering the Casc node
            val node = predefinedValidationStampsAdminContext.render()
            assertEquals(
                mapOf(
                    "replace" to false,
                    "list" to (1..3).map {
                        mapOf(
                            "name" to "VS_$it",
                            "description" to "PVS $it",
                            "image" to null,
                        )
                    }
                ).asJson(),
                node,
            )
        }
    }

    @Test
    fun `Adding a predefined validation stamp`() {
        val name = uid("vs_")
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-validation-stamps:
                        replace: false
                        list:
                            - name: $name
                              description: Sample predefined test called $name
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedValidationStampService.findPredefinedValidationStampByName(name).getOrNull(),
            "$name predefined VS has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
        }
    }

    @Test
    fun `Replacing the predefined validation stamps`() {
        val existing = uid("vs_")
        val name = uid("vs_")
        // Creates an existing PVS
        asAdmin {
            predefinedValidationStampService.newPredefinedValidationStamp(
                PredefinedValidationStamp.of(NameDescription.nd(existing, "Existing"))
            )
        }
        // Runs the Casc configuration
        casc(
            """
            ontrack:
                admin:
                    predefined-validation-stamps:
                        replace: true
                        list:
                            - name: $name
                              description: Sample predefined test called $name
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedValidationStampService.findPredefinedValidationStampByName(name).getOrNull(),
            "$name predefined VS has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
        }
        // Checks that the existing PVS is not present any longer
        assertNull(
            predefinedValidationStampService.findPredefinedValidationStampByName(existing).getOrNull(),
            "Existing PVS have been removed"
        )
    }

    @Test
    fun `Not replacing existing images`() {
        val existing = uid("vs_")
        // Creates an existing PVS
        asAdmin {
            val PVS = predefinedValidationStampService.newPredefinedValidationStamp(
                PredefinedValidationStamp.of(NameDescription.nd(existing, "Existing"))
            )
            predefinedValidationStampService.setPredefinedValidationStampImage(
                PVS.id,
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
                    predefined-validation-stamps:
                        replace: false
                        list:
                            - name: $existing
                              description: Sample predefined test called $existing
        """.trimIndent()
        )
        // Checks that the new predefined element is still there
        assertNotNull(
            predefinedValidationStampService.findPredefinedValidationStampByName(existing).getOrNull(),
            "$existing predefined VS has been kept"
        ) {
            assertEquals("Sample predefined test called $existing", it.description)
            val storedImage = predefinedValidationStampService.getPredefinedValidationStampImage(it.id)
            assertFalse(storedImage.isEmpty, "Image has been saved")
            val expectedImage = resourceBytes("/casc/bronze.png")
            assertContentEquals(expectedImage, storedImage.content, "Expected image content")
        }
    }

    @Test
    fun `Predefined validation stamps using an image`() {
        val name = uid("vs_")
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
                    predefined-validation-stamps:
                        replace: false
                        list:
                            - name: $name
                              description: Sample predefined test called $name
                              image: scm://test/$config/casc/bronze.png
        """.trimIndent()
        )
        // Checks that the new predefined element is created
        assertNotNull(
            predefinedValidationStampService.findPredefinedValidationStampByName(name).getOrNull(),
            "$name predefined VS has been created"
        ) {
            assertEquals("Sample predefined test called $name", it.description)
            val storedImage = predefinedValidationStampService.getPredefinedValidationStampImage(it.id)
            assertFalse(storedImage.isEmpty, "Image has been saved")
            val expectedImage = resourceBytes("/casc/bronze.png")
            assertContentEquals(expectedImage, storedImage.content, "Expected image content")
        }
    }

}