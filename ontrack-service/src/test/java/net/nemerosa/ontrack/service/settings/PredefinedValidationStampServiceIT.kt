package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedValidationStampNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ValidationStampBulkUpdate
import net.nemerosa.ontrack.model.security.ValidationStampEdit
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.model.structure.config
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class PredefinedValidationStampServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var service: PredefinedValidationStampService

    @Autowired
    private lateinit var testNumberValidationDataType: TestNumberValidationDataType

    @Test
    fun `Predefined validation stamp creation`() {
        val name = uid("PVS")
        val predefinedValidationStamp = asUser().with(GlobalSettings::class.java).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        assertNotNull(predefinedValidationStamp) {
            assertTrue(it.id.isSet)
            assertNull(it.dataType)
        }
    }

    @Test
    fun `Predefined validation stamp creation with data type`() {
        val name = uid("PVS")
        asUser().with(GlobalSettings::class.java).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    ).withDataType(testNumberValidationDataType.config(50))
            )
        }

        val predefinedValidationStamp = service.findPredefinedValidationStampByName(name)

        assertPresent(predefinedValidationStamp) {
            assertTrue(it.id.isSet)
            assertNotNull(it.dataType) {
                assertEquals(50, it.config as Int)
            }
        }
    }

    @Test
    fun `Predefined validation stamp name already exists`() {
        val name = uid("PVS")
        // Once --> OK
        asUser().with(GlobalSettings::class.java).call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(
                            NameDescription.nd(
                                    name,
                                    "Predefined $name"
                            )
                    )
            )
        }
        // Twice --> NOK
        assertFailsWith<PredefinedValidationStampNameAlreadyDefinedException> {
            asUser().with(GlobalSettings::class.java).call {
                service.newPredefinedValidationStamp(
                        PredefinedValidationStamp.of(
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
    fun `Bulk update of validation stamps creates a predefined validation stamp`() {
        // Creates three validation stamps, two with the same name, one with a different name
        val vs1Name = uid("VS")
        val vs2Name = uid("VS")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val branch3 = doCreateBranch()
        val vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ""))
        val vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ""))
        val vs3 = doCreateValidationStamp(branch3, NameDescription.nd(vs2Name, ""))

        // Updates the VS1 description and image
        asUser().with(vs1, ValidationStampEdit::class.java).call {
            structureService.saveValidationStamp(
                    vs1.withDescription("My new description")
            )
            structureService.setValidationStampImage(
                    vs1.id,
                    Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
            )
        }

        // Bulk update
        asUser().with(ValidationStampBulkUpdate::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getValidationStamp(vs2.id).description)
            assertTrue(structureService.getValidationStamp(vs2.id).isImage)
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getValidationStamp(vs3.id).description)
            assertFalse(structureService.getValidationStamp(vs3.id).isImage)
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertEquals("My new description", it.description)
                assertTrue(it.isImage)
            }
        }
    }

    @Test
    fun `Bulk update of validation stamps without an image creates a predefined validation stamp without an image`() {
        // Creates three validation stamps, two with the same name, one with a different name
        val vs1Name = uid("VS")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ""))
        val vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ""))

        // Updates the VS2 description and image
        asUser().with(vs2, ValidationStampEdit::class.java).call {
            structureService.setValidationStampImage(
                    vs2.id,
                    Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
            )
        }

        // Bulk update
        asUser().with(ValidationStampBulkUpdate::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated with no image
        asAdmin().call {
            assertFalse(structureService.getValidationStamp(vs2.id).isImage, "VS2 image is gone")
        }

        // Checks the predefined validation stamp has been created, without an image
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertFalse(it.isImage, "Created predefined validation stamp has no image")
            }
        }
    }

    @Test
    fun `Bulk update of validation stamps with a data type creates a predefined validation stamp with a data type`() {
        // Creates three validation stamps, two with the same name, one with a different name
        val vs1Name = uid("VS")
        val vs2Name = uid("VS")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val branch3 = doCreateBranch()
        val vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ""), testNumberValidationDataType.config(10))
        val vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ""), testNumberValidationDataType.config(20))
        val vs3 = doCreateValidationStamp(branch3, NameDescription.nd(vs2Name, ""), testNumberValidationDataType.config(30))

        // Bulk update
        asUser().with(ValidationStampBulkUpdate::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            structureService.getValidationStamp(vs2.id).apply {
                assertNotNull(dataType) {
                    assertEquals(10, it.config as Int)
                }
            }
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            structureService.getValidationStamp(vs3.id).apply {
                assertNotNull(dataType) {
                    assertEquals(30, it.config as Int)
                }
            }
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertNotNull(it.dataType) {
                    assertEquals(10, it.config as Int)
                }
            }
        }
    }

    @Test
    fun `Bulk update of validation stamps updates a predefined validation stamp`() {
        // Creates three validation stamps, two with the same name, one with a different name
        val vs1Name = uid("VS")
        val vs2Name = uid("VS")
        val branch1 = doCreateBranch()
        val branch2 = doCreateBranch()
        val branch3 = doCreateBranch()
        val vs1 = doCreateValidationStamp(branch1, NameDescription.nd(vs1Name, ""))
        val vs2 = doCreateValidationStamp(branch2, NameDescription.nd(vs1Name, ""))
        val vs3 = doCreateValidationStamp(branch3, NameDescription.nd(vs2Name, ""))
        // Predefined validation stamp
        val pvs = asAdmin().call {
            service.newPredefinedValidationStamp(
                    PredefinedValidationStamp.of(NameDescription.nd(vs1Name, ""))
            )
        }

        // Updates the VS1 description and image
        asUser().with(vs1, ValidationStampEdit::class.java).call {
            structureService.saveValidationStamp(
                    vs1.withDescription("My new description")
            )
            structureService.setValidationStampImage(
                    vs1.id,
                    Document("image/png", TestUtils.resourceBytes("/validationStampImage1.png"))
            )
        }

        // Bulk update
        asUser().with(ValidationStampBulkUpdate::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getValidationStamp(vs2.id).description)
            assertTrue(structureService.getValidationStamp(vs2.id).isImage)
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getValidationStamp(vs3.id).description)
            assertFalse(structureService.getValidationStamp(vs3.id).isImage)
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertEquals(pvs.id, it.id)
                assertEquals("My new description", it.description)
                assertTrue(it.isImage)
            }
        }
    }

}