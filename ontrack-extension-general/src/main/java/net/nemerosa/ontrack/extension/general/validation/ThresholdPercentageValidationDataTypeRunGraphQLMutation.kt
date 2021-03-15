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
class ThresholdPercentageValidationDataTypeRunGraphQLMutation(
    structureService: StructureService,
    validationRunStatusService: ValidationRunStatusService,
    runInfoService: RunInfoService,
) : AbstractTypedValidationRunMutationProvider<Int>(
    structureService,
    validationRunStatusService,
    runInfoService,
) {

    override val mutationFragmentName: String = "Percentage"

    override val dataType: KClass<out ValidationDataType<*, Int>> =
        ThresholdPercentageValidationDataType::class

    override val dataInputFields: List<GraphQLInputObjectField> = listOf(
        requiredIntInputField("value", "Percentage"),
    )

    override fun readInput(input: EnvMutationInput) =
        input.getRequiredInput<Int>("value")
}