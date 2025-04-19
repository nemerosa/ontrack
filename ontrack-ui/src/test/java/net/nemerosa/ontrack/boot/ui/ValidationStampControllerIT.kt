package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType
//import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
//import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.security.ValidationStampCreate
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ValidationStampInput
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.fail

class ValidationStampControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var validationStampController: ValidationStampController

    @Test
    fun `Deleting a validation stamp when global validation stamp manager`() {
        project {
            branch {
                val vs = validationStamp()
                asAccountWithGlobalRole(Roles.GLOBAL_VALIDATION_MANAGER) {
                    validationStampController.deleteValidationStamp(vs.id)
                }
                // Checks the VS has been deleted
                assertFailsWith<ValidationStampNotFoundException> {
                    structureService.getValidationStamp(vs.id)
                }
            }
        }
    }

    @Test
    fun `Deleting a validation stamp when global validation stamp manager and when not using global view`() {
        withNoGrantViewToAll {
            project {
                branch {
                    val vs = validationStamp()
                    asAccountWithGlobalRole(Roles.GLOBAL_VALIDATION_MANAGER) {
                        validationStampController.deleteValidationStamp(vs.id)
                    }
                    // Checks the VS has been deleted
                    assertFailsWith<ValidationStampNotFoundException> {
                        structureService.getValidationStamp(vs.id)
                    }
                }
            }
        }
    }

    @Test
    fun `Deleting a validation stamp when project validation manager and when not using global view`() {
        withNoGrantViewToAll {
            project {
                branch {
                    val vs = validationStamp()
                    asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                        validationStampController.deleteValidationStamp(vs.id)
                    }
                    // Checks the VS has been deleted
                    assertFailsWith<ValidationStampNotFoundException> {
                        structureService.getValidationStamp(vs.id)
                    }
                }
            }
        }
    }

    @Test
    @Disabled("Missing General extension")
    fun `Deleting a validation stamp when project validation manager, when not using global view and when validation stamp is used for auto promotion`() {
//        withNoGrantViewToAll {
//            project {
//                branch {
//                    val vs = validationStamp()
//                    val other = validationStamp()
//                    val pl = promotionLevel {
//                        setProperty(this, AutoPromotionPropertyType::class.java,
//                            AutoPromotionProperty(
//                                validationStamps = listOf(vs, other),
//                                include = "",
//                                exclude = "",
//                                promotionLevels = emptyList()
//                            )
//                        )
//                    }
//                    asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
//                        validationStampController.deleteValidationStamp(vs.id)
//                    }
//                    // Checks the VS has been deleted
//                    assertFailsWith<ValidationStampNotFoundException> {
//                        structureService.getValidationStamp(vs.id)
//                    }
//                    // Checks that the PL does not contain the deleted VS any longer
//                    assertNotNull(getProperty(pl, AutoPromotionPropertyType::class.java)) { property ->
//                        assertEquals(
//                            listOf(other.name),
//                            property.validationStamps.map { it.name }
//                        )
//                    }
//                }
//            }
//        }
    }

    @Test
    fun `Edition form for a validation stamp with type`() {
        // Branch
        val branch = doCreateBranch()
        // Creates a validation stamp with an associated percentage data type
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            validationStampController.newValidationStamp(
                branch.id,
                ValidationStampInput(
                    "VSPercent",
                    "",
                    ServiceConfiguration(
                        TestNumberValidationDataType::class.java.name,
                        JsonUtils.format(mapOf("threshold" to 60))
                    )
                )
            )
        }.body ?: fail("Cannot find validation stamp")
        // Gets the edition form
        val form = asUser { validationStampController.updateValidationStampForm(vs.id) }
        // Gets the service configurator field
        val field = form.fields.find { it.type == "service-configurator" }
        assertNotNull(field) {
            // Checks that the field value is a service configuration
            assertIs<ServiceConfiguration>(it.value) {
                assertEquals("net.nemerosa.ontrack.extension.api.support.TestNumberValidationDataType", it.id)
                assertNotNull(it.data) {
                    assertEquals(60, it["threshold"].asInt())
                }
            }
        }
    }

    @Test
    fun validationStampWithDataType() {
        // Branch
        val branch = doCreateBranch()
        // Creates a validation stamp with an associated percentage data type
        val vs = asUser().with(branch, ValidationStampCreate::class.java).call {
            validationStampController.newValidationStamp(
                branch.id,
                ValidationStampInput(
                    "VSPercent",
                    "",
                    ServiceConfiguration(
                        TestNumberValidationDataType::class.java.name,
                        JsonUtils.format(mapOf("threshold" to 60))
                    )
                )
            )
        }.body ?: fail("Cannot find validation stamp")
        // Loads the validation stamp
        val loadedVs = asUser {
            validationStampController.getValidationStamp(vs.id)
        }.body ?: fail("Cannot find validation stamp")
        // Checks the data type is still there
        val dataType = loadedVs.dataType
        assertNotNull(dataType, "Data type is loaded")
        assertEquals(TestNumberValidationDataType::class.java.name, dataType.descriptor.id)
        assertEquals(60, dataType.config)
    }

}
