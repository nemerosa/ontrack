package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
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
                    .type(stdList(label.typeRef))
                    .dataFetcher {
                        labelManagementService.labels
                    }
                    .build()
}