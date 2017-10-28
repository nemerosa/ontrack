package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Associated of a [GQLTypeValidationStamp] together with the list
 * of associated [GQLTypeValidationRun] for a build.
 */
@Component
class GQLTypeValidation
@Autowired
constructor(
        private val validationStamp: GQLTypeValidationStamp,
        private val validationRun: GQLTypeValidationRun
) : GQLType {

    companion object {
        @JvmField
        val VALIDATION = "Validation"
    }

    override fun getTypeRef() = GraphQLTypeReference(VALIDATION)

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
                        .argument {
                            it.name("count")
                                    .description("Maximum number of validation runs")
                                    .type(Scalars.GraphQLInt)
                                    .defaultValue(50)
                        }
                        .type(GraphqlUtils.stdList(validationRun.typeRef))
                        .dataFetcher(GraphqlUtils.fetcher(
                                GQLTypeValidationData::class.java,
                                { environment, data ->
                                    val count = GraphqlUtils.getIntArgument(environment, "count")
                                    if (count.isPresent) {
                                        data.validationRuns.take(count.asInt)
                                    } else {
                                        data.validationRuns
                                    }
                                }
                        ))
            }
            // OK
            .build()

    data class GQLTypeValidationData(
            val validationStamp: ValidationStamp,
            val validationRuns: List<ValidationRun>
    )

}
