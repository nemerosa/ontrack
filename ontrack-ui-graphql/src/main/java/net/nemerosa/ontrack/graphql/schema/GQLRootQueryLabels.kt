package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.labels.LabelManagementService
import org.springframework.stereotype.Component

/**
 * Gets the list of labels
 */
@Component
class GQLRootQueryLabels(
        private val label: GQLTypeLabel,
        private val labelManagementService: LabelManagementService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("labels")
                    .description("List of all labels")
                    .type(listType(label.typeRef))
                    .argument {
                        it.name("category")
                                .description("Category to look for")
                                .type(GraphQLString)
                    }
                    .argument {
                        it.name("name")
                                .description("Name to look for")
                                .type(GraphQLString)
                    }
                    .dataFetcher { environment ->
                        val category: String? = environment.getArgument("category")
                        val name: String? = environment.getArgument("name")
                        if (category != null || name != null) {
                            labelManagementService.findLabels(category, name)
                        } else {
                            labelManagementService.labels
                        }
                    }
                    .build()
}