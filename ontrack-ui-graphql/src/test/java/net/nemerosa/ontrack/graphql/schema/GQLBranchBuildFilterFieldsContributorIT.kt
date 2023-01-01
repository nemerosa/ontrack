package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLBranchBuildFilterFieldsContributorIT: AbstractQLKTITSupport() {

    @Test
    fun `Getting the list of build filter forms for a branch`() {
        project {
            branch {
                run("""
                    {
                        branches(id: $id) {
                            buildFilterForms {
                                type
                                typeName
                                isPredefined
                                form
                            }
                        }
                    }
                """) { data ->
                    val branch = data.path("branches").path(0)
                    val forms = branch.path("buildFilterForms")
                    // Looks for the standard filter
                    assertNotNull(
                            forms.find {
                                it.getRequiredTextField("type") == "net.nemerosa.ontrack.service.StandardBuildFilterProvider"
                            },
                            "Found the standard filter form"
                    ) { filter ->
                        assertEquals("Standard filter", filter.getRequiredTextField("typeName"))
                        assertEquals(false, filter.getRequiredBooleanField("isPredefined"))
                        val form = filter.getRequiredJsonField("form")
                        // Checks some fields
                        val fields = form.getRequiredJsonField("fields")
                        assertNotNull(fields.find {
                            it.getRequiredTextField("name") == "name"
                        }, "Name field")
                        assertNotNull(fields.find {
                            it.getRequiredTextField("name") == "withPromotionLevel"
                        }, "With promotion level field")
                    }
                }
            }
        }
    }

}
