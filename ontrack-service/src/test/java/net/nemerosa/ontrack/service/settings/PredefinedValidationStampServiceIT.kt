package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.exceptions.PredefinedValidationStampNameAlreadyDefinedException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ValidationStampEdit
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.*

class PredefinedValidationStampServiceIT : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var service: PredefinedValidationStampService

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
        asUser().with(GlobalSettings::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getValidationStamp(vs2.id).description)
            assertTrue(structureService.getValidationStamp(vs2.id).image)
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getValidationStamp(vs3.id).description)
            assertFalse(structureService.getValidationStamp(vs3.id).image)
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertEquals("My new description", it.description)
                assertTrue(it.image)
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
        asUser().with(GlobalSettings::class.java).call {
            structureService.bulkUpdateValidationStamps(vs1.id)
        }

        // Checks the validation stamp with same name has been updated
        asAdmin().call {
            assertEquals("My new description", structureService.getValidationStamp(vs2.id).description)
            assertTrue(structureService.getValidationStamp(vs2.id).image)
        }

        // Checks the validation stamp with NOT same name has NOT been updated
        asAdmin().call {
            assertEquals("", structureService.getValidationStamp(vs3.id).description)
            assertFalse(structureService.getValidationStamp(vs3.id).image)
        }

        // Checks the predefined validation stamp has been created
        asAdmin().call {
            val o = service.findPredefinedValidationStampByName(vs1.name)
            assertPresent(o) {
                assertEquals(pvs.id, it.id)
                assertEquals("My new description", it.description)
                assertTrue(it.image)
            }
        }
    }

}