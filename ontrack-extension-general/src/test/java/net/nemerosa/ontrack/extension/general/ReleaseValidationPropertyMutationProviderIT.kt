package net.nemerosa.ontrack.extension.general

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReleaseValidationPropertyMutationProviderIT : AbstractGeneralExtensionTestSupport() {

    @Test
    fun `Setting the release validation property on a branch by name`() {
        asAdmin {
            project {
                branch {
                    run(
                        """
                            mutation {
                                setBranchReleaseValidationProperty(input: {
                                    project: "${project.name}",
                                    branch: "$name",
                                    validation: "labelled"
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """
                    ).let { data ->
                        assertNoUserError(data, "setBranchReleaseValidationPropertyByName")
                        assertNotNull(getProperty(this, ReleaseValidationPropertyType::class.java)) { property ->
                            assertEquals("labelled", property.validation)
                        }
                    }
                }
            }
        }
    }

}