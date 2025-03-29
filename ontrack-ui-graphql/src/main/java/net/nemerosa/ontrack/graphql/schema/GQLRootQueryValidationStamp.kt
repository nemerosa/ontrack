package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import net.nemerosa.ontrack.graphql.support.intArgument
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
                    intArgument("id" , "ID of the validation stamp to look for (required)", nullable = false)
                )
                .dataFetcher { environment ->
                    // Gets the ID
                    val id: Int = environment.getArgument("id")!!
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
