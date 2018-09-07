package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

/**
 * Root query to get a [ValidationStamp] using its ID.
 */
@Component
class GQLRootQueryValidationStamp(
        private val structureService: StructureService,
        private val validationStamp: GQLTypeValidationStamp
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
                .name("validationStamp")
                .type(validationStamp.typeRef)
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the validation stamp to look for (required)")
                                .type(GraphQLNonNull(GraphQLInt))
                                .build()
                )
                .dataFetcher { environment ->
                    // Gets the ID
                    val id = GraphqlUtils.getIntArgument(environment, "id")
                            .orElseThrow { IllegalStateException("`id` argument is required") }
                    // Gets the validation stamp
                    try {
                        structureService.getValidationStamp(ID.of(id))
                    } catch (ignored: ValidationStampNotFoundException) {
                        null
                    }
                }
                .build()
    }

}
