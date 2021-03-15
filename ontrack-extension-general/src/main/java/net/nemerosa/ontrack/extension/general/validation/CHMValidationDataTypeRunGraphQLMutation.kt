package net.nemerosa.ontrack.extension.general.validation

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.AbstractTypedValidationRunMutationProvider
import net.nemerosa.ontrack.graphql.schema.EnvMutationInput
import net.nemerosa.ontrack.graphql.schema.optionalIntInputField
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class CHMValidationDataTypeRunGraphQLMutation(
    structureService: StructureService,
    validationRunStatusService: ValidationRunStatusService,
    runInfoService: RunInfoService,
) : AbstractTypedValidationRunMutationProvider<CHMLValidationDataTypeData>(
    structureService,
    validationRunStatusService,
    runInfoService,
) {

    override val mutationFragmentName: String = "CHML"

    override val dataType: KClass<out ValidationDataType<*, CHMLValidationDataTypeData>> = CHMLValidationDataType::class

    override val dataInputFields: List<GraphQLInputObjectField> = listOf(
        optionalIntInputField("critical", "Number of critical issues"),
        optionalIntInputField("high", "Number of high issues"),
        optionalIntInputField("medium", "Number of medium issues"),
        optionalIntInputField("low", "Number of low issues"),
    )

    override fun readInput(input: EnvMutationInput): CHMLValidationDataTypeData {
        return CHMLValidationDataTypeData(
            mapOf(
                CHML.CRITICAL to (input.getInput<Int>("critical") ?: 0),
                CHML.HIGH to (input.getInput<Int>("high") ?: 0),
                CHML.MEDIUM to (input.getInput<Int>("medium") ?: 0),
                CHML.LOW to (input.getInput<Int>("low") ?: 0),
            )
        )
    }
}