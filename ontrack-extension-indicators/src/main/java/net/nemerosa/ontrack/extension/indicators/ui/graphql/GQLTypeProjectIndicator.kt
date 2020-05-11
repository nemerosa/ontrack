package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.Rating
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicator(
        private val projectIndicatorType: GQLTypeProjectIndicatorType,
        private val signature: GQLTypeCreation,
        private val fieldContributors: List<GQLFieldContributor>,
        private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
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
                it.name(ProjectIndicator::compliance.name)
                        .description("Compliance for the indicator")
                        .type(GraphQLInt)
                        .dataFetcher { env ->
                            env.getSource<ProjectIndicator>().compliance?.value
                        }
            }
            .field {
                it.name(ProjectIndicator::comment.name)
                        .description("Comment for the indicator")
                        .type(GraphQLString)
            }
            .field {
                it.name("annotatedComment")
                        .type(GraphQLString)
                        .description("Comment with links.")
                        .dataFetcher { env ->
                            val projectIndicator = env.getSource<ProjectIndicator>()
                            val comment = projectIndicator.comment
                            if (comment.isNullOrBlank()) {
                                comment
                            } else {
                                annotatedDescription(projectIndicator.project, comment)
                            }
                        }
            }
            .field {
                it.name(ProjectIndicator::signature.name)
                        .description("Signature for the indicator")
                        .type(signature.typeRef)
                        .dataFetcher(GQLTypeCreation.dataFetcher(ProjectIndicator::signature))
            }
            // Rating
            .field {
                it.name("rating")
                        .description("Rating for this indicator")
                        .type(GraphQLString)
                        .dataFetcher { env ->
                            env.getSource<ProjectIndicator>().compliance?.let { compliance ->
                                Rating.asRating(compliance.value)
                            }
                        }
            }
            // Links
            .fields(ProjectIndicator::class.java.graphQLFieldContributions(fieldContributors))
            .build()


    private fun annotatedDescription(project: Project, comment: String): String {
        // Gets the list of message annotators to use
        val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(project) }
        // Annotates the message
        return MessageAnnotationUtils.annotate(comment, annotators)
    }

    override fun getTypeName(): String = ProjectIndicator::class.java.simpleName
}