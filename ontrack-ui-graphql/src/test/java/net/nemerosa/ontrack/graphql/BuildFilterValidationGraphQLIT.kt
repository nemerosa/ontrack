package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BuildFilterValidationGraphQLIT : AbstractQLKTITSupport() {

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
                buildFilterValidation(filter),
                "No validation error"
        )
        // Runs some code to invalid the filter
        asAdmin().execute { invalidationCode() }
        // Validation
        assertEquals(
                expectedMessage,
                buildFilterValidation(filter)
        )
    }

    private fun Branch.buildFilterValidation(filter: StandardBuildFilterData): String? {
        val dataJSON: String = JsonUtils.toJSONString(filter)
        val data = run("""
            query BuildFilterValidationQuery(
                ${'$'}branchId: Int!,,
                ${'$'}type: String!,
                ${'$'}data: String!) {
                buildFilterValidation(branchId: ${'$'}branchId,
                    filter: {type: ${'$'}type, data: ${'$'}data}) {
                    error
                }
            }
        """, mapOf(
                "branchId" to id(),
                "type" to "net.nemerosa.ontrack.service.StandardBuildFilterProvider",
                "data" to dataJSON
        ))
        return data["buildFilterValidation"]["error"].asText(null)
    }

}