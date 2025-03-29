package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

/**
 * Associated of a [GQLTypeValidationStamp] together with the list
 * of associated [GQLTypeValidationRun] for a build.
 */
@Component
class GQLTypeValidation(
    private val validationStamp: GQLTypeValidationStamp,
    private val validationRun: GQLTypeValidationRun
) : GQLType {

    companion object {
        const val VALIDATION = "Validation"
    }

    override fun getTypeName() = VALIDATION

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(VALIDATION)
        // Validation stamp
        .field {
            it.name("validationStamp")
                .description("Associated validation stamp")
                .type(validationStamp.typeRef)
        }
        // Validation runs
        .field {
            it.name("validationRuns")
                .description("Associated validation runs")
                .argument { a ->
                    a.name("count")
                        .description("Maximum number of validation runs")
                        .type(Scalars.GraphQLInt)
                        .defaultValue(50)
                }
                .type(listType(validationRun.typeRef))
                .dataFetcher { env ->
                    val data: GQLTypeValidationData = env.getSource()!!
                    val count: Int? = env.getArgument("count")
                    if (count != null) {
                        data.validationRuns.take(count)
                    } else {
                        data.validationRuns
                    }
                }
        }
        // OK
        .build()

    data class GQLTypeValidationData(
        val validationStamp: ValidationStamp,
        val validationRuns: List<ValidationRun>
    )

}
