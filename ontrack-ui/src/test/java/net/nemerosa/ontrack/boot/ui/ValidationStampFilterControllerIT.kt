package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ValidationStampFilterControllerIT : AbstractWebTestSupport() {

    @Autowired
    private lateinit var controller: ValidationStampFilterController

    @Test
    fun `New filter contains all validation stamp filters`() {
        // Filter name
        val name = uid("F")
        // Creates a branch and some validation stamps
        project {
            branch {
                (1..10).forEach {
                    validationStamp("VS$it")
                }
                // Creates a new validation stamp filter...
                val filter: ValidationStampFilter = asUser().with(this, ProjectConfig::class.java).call {
                    controller.createBranchValidationStampFilterForm(id, nd(name, ""))
                }
                // Checks that all validation stamps names are included
                assertEquals(
                        (1..10).map { "VS$it" },
                        filter.vsNames
                )
            }
        }
    }
}