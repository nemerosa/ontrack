package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class GQLRootQueryValidationRuns(
        private val structureService: StructureService,
        private val validationRun: GQLTypeValidationRun
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
                .name("validationRuns")
                .type(stdList(validationRun.typeRef))
                .argument {
                    it.name("id")
                            .description("ID of the validation run to look for")
                            .type(GraphQLNonNull(GraphQLInt))
                }
                .dataFetcher(validationRunFetcher())
                .build()
    }

    private fun validationRunFetcher(): DataFetcher<List<ValidationRun>> {
        return DataFetcher { environment ->
            val id: Int? = environment.getArgument("id")
            if (id != null) {
                // Fetch by ID
                listOf(structureService.getValidationRun(ID.of(id)))
            } else {
                emptyList()
            }
        }
    }

}
