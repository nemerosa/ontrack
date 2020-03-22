package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StandardBuildFilterProviderIT : AbstractDSLTestSupport() {


    @Test
    fun `Limit the count of builds`() {
        project {
            branch {
                // Creates 20 builds
                (1..20).forEach {
                    build("$it")
                }
                // Limit the number of builds to 5
                val oldLimit = ontrackConfigProperties.buildFilterCountMax
                try {
                    ontrackConfigProperties.buildFilterCountMax = 5
                    // Getting 1000 builds
                    val filter = buildFilterService.standardFilterProviderData(1000).build()
                    val builds = filter.filterBranchBuilds(this)
                    // Checks that only 5 builds are returned
                    assertEquals(5, builds.size)
                } finally {
                    ontrackConfigProperties.buildFilterCountMax = oldLimit
                }
            }
        }
    }

    @Test
    fun `Filter validation for since promotion field`() {
        project {
            branch {
                val pl = promotionLevel()
                val filter = StandardBuildFilterData.of(1).withSincePromotionLevel(pl.name)
                validationError(
                        filter,
                        """Promotion level ${pl.name} does not exist for filter "Since promotion"."""
                ) {
                    structureService.deletePromotionLevel(pl.id)
                }
            }
        }
    }

    @Test
    fun `Filter validation for with promotion field`() {
        project {
            branch {
                val pl = promotionLevel()
                val filter = StandardBuildFilterData.of(1).withWithPromotionLevel(pl.name)
                validationError(
                        filter,
                        """Promotion level ${pl.name} does not exist for filter "With promotion"."""
                ) {
                    structureService.deletePromotionLevel(pl.id)
                }
            }
        }
    }

    @Test
    fun `Filter validation for since validation field`() {
        project {
            branch {
                val vs = validationStamp()
                val filter = StandardBuildFilterData.of(1).withSinceValidationStamp(vs.name)
                validationError(
                        filter,
                        """Validation stamp ${vs.name} does not exist for filter "Since validation"."""
                ) {
                    structureService.deleteValidationStamp(vs.id)
                }
            }
        }
    }

    @Test
    fun `Filter validation for with validation field`() {
        project {
            branch {
                val vs = validationStamp()
                val filter = StandardBuildFilterData.of(1).withWithValidationStamp(vs.name)
                validationError(
                        filter,
                        """Validation stamp ${vs.name} does not exist for filter "With validation"."""
                ) {
                    structureService.deleteValidationStamp(vs.id)
                }
            }
        }
    }

    private fun Branch.validationError(
            filter: StandardBuildFilterData,
            expectedMessage: String,
            invalidationCode: () -> Unit
    ) {
        // Validation before code
        assertNull(
                buildFilterService.validateBuildFilterProviderData(
                        this,
                        StandardBuildFilterProvider::class.java.name,
                        JsonUtils.format(filter)
                ),
                "No validation error"
        )
        // Runs some code to invalid the filter
        asAdmin().execute { invalidationCode() }
        // Validation
        assertEquals(
                expectedMessage,
                buildFilterService.validateBuildFilterProviderData(
                        this,
                        StandardBuildFilterProvider::class.java.name,
                        JsonUtils.format(filter)
                )
        )
    }

}