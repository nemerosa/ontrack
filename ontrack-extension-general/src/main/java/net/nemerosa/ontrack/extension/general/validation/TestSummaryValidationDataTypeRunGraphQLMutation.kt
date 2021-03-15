package net.nemerosa.ontrack.extension.general.validation

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.AbstractTypedValidationRunMutationProvider
import net.nemerosa.ontrack.graphql.schema.EnvMutationInput
import net.nemerosa.ontrack.graphql.schema.requiredIntInputField
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class TestSummaryValidationDataTypeRunGraphQLMutation(
    structureService: StructureService,
    validationRunStatusService: ValidationRunStatusService,
    runInfoService: RunInfoService,
) : AbstractTypedValidationRunMutationProvider<TestSummaryValidationData>(
    structureService,
    validationRunStatusService,
    runInfoService,
) {

    override val mutationFragmentName: String = "Tests"

    override val dataType: KClass<out ValidationDataType<*, TestSummaryValidationData>> =
        TestSummaryValidationDataType::class

    override val dataInputFields: List<GraphQLInputObjectField> = listOf(
        requiredIntInputField("passed", "Count of passed tests"),
        requiredIntInputField("skipped", "Count of skipped tests"),
        requiredIntInputField("failed", "Count of failed tests"),
    )

    override fun readInput(input: EnvMutationInput) = TestSummaryValidationData(
        passed = input.getRequiredInput("passed"),
        skipped = input.getRequiredInput("skipped"),
        failed = input.getRequiredInput("failed"),
    )
}