package net.nemerosa.ontrack.extension.general.validation

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.graphql.schema.AbstractTypedValidationStampMutationProvider
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.config
import org.springframework.stereotype.Component

/**
 * Provides the `setupMetricsValidationStamp` mutation to setup a validation stamp
 */
@Component
class MetricsValidationDataTypeGraphQLMutation(
    structureService: StructureService,
    private val metricsValidationDataType: MetricsValidationDataType
) : AbstractTypedValidationStampMutationProvider<Any?>(structureService) {

    override val mutationFragmentName: String = "Metrics"

    override val dataTypeInputFields: List<GraphQLInputObjectField> = emptyList()

    override fun readInput(input: MutationInput): ValidationDataTypeConfig<Any?> =
        metricsValidationDataType.config(null)
}