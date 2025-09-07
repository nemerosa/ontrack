package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.security.ValidationStampFilterMgt
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.ValidationStampFilterService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class ValidationStampFilterMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var validationStampFilterService: ValidationStampFilterService

    @Test
    @AsAdminTest
    fun `Deletion of a validation stamp filter`() {
        project {
            branch {
                val filterName = uid("vsf-")
                val vsf = validationStampFilterService.newValidationStampFilter(
                    ValidationStampFilter(
                        branch = this,
                        name = filterName,
                        vsNames = emptyList(),
                    )
                )
                asUser().withView(this).withProjectFunction(this, ValidationStampFilterMgt::class.java).call {
                    run(
                        """
                            mutation DeleteValidationStampFilter {
                                deleteValidationStampFilterById(input: {id: ${vsf.id}}) {
                                    errors { message }
                                }
                            }
                        """
                    ) { data ->
                        checkGraphQLUserErrors(data, "deleteValidationStampFilterById")
                        assertTrue(
                            validationStampFilterService.getBranchValidationStampFilters(this, true)
                                .none { it.name == filterName },
                            "Filter has been deleted"
                        )
                    }
                }
            }
        }
    }

}