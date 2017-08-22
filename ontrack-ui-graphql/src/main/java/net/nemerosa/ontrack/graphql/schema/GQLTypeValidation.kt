package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLObjectType
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

    override fun getType(): GraphQLObjectType = GraphQLObjectType.newObject()
            .name("Validation")
            // Validation stamp
            .field {
                it.name("validationStamp")
                        .description("Associated validation stamp")
                        .type(validationStamp.type)
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
                        .type(GraphqlUtils.stdList(validationRun.type))
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
