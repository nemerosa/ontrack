package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class BackValidationAutoVersioningCompletionListenerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var listener: BackValidationAutoVersioningCompletionListener

    @Test
    fun `Validation stamp is created if not existing`() {
        project {
            branch {
                build {
                    val source = this

                    project {
                        branch {
                            val branch = this

                            val order = branch.createOrder(
                                sourceProject = source.project.name,
                                sourceBackValidation = "back-validation",
                                sourceBuildId = source.id(),
                            )

                            listener.onAutoVersioningCompletion(order, AutoVersioningProcessingOutcome.CREATED)

                            val vs = structureService.findValidationStampByName(
                                project = source.project.name,
                                branch = source.branch.name,
                                validationStamp = "back-validation"
                            ).getOrNull()
                            assertNotNull(vs, "Validation stamp has been created") {
                                val run = structureService.getValidationRunsForBuildAndValidationStamp(source, it, 0, 1)
                                    .firstOrNull()
                                assertNotNull(run, "Validation has been created") { r ->
                                    assertEquals(ValidationRunStatusID.STATUS_PASSED, r.lastStatus.statusID)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

}