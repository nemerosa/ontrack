package net.nemerosa.ontrack.extension.general.validation

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.AbstractTypedValidationStampMutationProvider
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.optionalBooleanInputField
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.config
import org.springframework.stereotype.Component

/**
 * Provides the `setupTestSummaryValidationStamp` mutation to setup a validation stamp
 */
@Component
class TestSummaryValidationDataTypeGraphQLMutation(
    structureService: StructureService,
    private val testSummaryValidationDataType: TestSummaryValidationDataType
) : AbstractTypedValidationStampMutationProvider<TestSummaryValidationConfig>(structureService) {

    override val mutationFragmentName: String = "TestSummary"

    override val dataTypeInputFields: List<GraphQLInputObjectField> = listOf(
        optionalBooleanInputField(
            TestSummaryValidationConfig::warningIfSkipped.name,
            "If set to true, the status is set to warning if there is at least one skipped test."
        )
    )

    override fun readInput(input: MutationInput): ValidationDataTypeConfig<TestSummaryValidationConfig> {
        return testSummaryValidationDataType.config(
            TestSummaryValidationConfig(
                warningIfSkipped = input.getInput<Boolean>(TestSummaryValidationConfig::warningIfSkipped.name) ?: false
            )
        )
    }
}