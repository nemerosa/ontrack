package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicator(
        private val projectIndicatorType: GQLTypeProjectIndicatorType,
        private val signature: GQLTypeCreation,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Project indicator")
            .field {
                it.name(ProjectIndicator::type.name)
                        .description("Type of indicator")
                        .type(projectIndicatorType.typeRef)
            }
            .field {
                it.name(ProjectIndicator::value.name)
                        .description("Value for the indicator")
                        .type(GQLScalarJSON.INSTANCE)
            }
            .field {
                it.name(ProjectIndicator::status.name)
                        .description("Status for the indicator")
                        .type(GraphQLString)
            }
            .field {
                it.name(ProjectIndicator::comment.name)
                        .description("Comment for the indicator")
                        .type(GraphQLString)
            }
            .field {
                it.name(ProjectIndicator::signature.name)
                        .description("Signature for the indicator")
                        .type(signature.typeRef)
                        .dataFetcher(GQLTypeCreation.dataFetcher<ProjectIndicator> { it.signature })
            }
            .fields(ProjectIndicator::class.java.graphQLFieldContributions(fieldContributors))
            .build()

    override fun getTypeName(): String = ProjectIndicator::class.java.simpleName
}