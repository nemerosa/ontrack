package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.model.Rating
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeCreation
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class GQLTypeProjectIndicatorHistoryItem(
        private val projectIndicatorType: GQLTypeProjectIndicatorType,
        private val signature: GQLTypeCreation,
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
                        .type(Scalars.GraphQLInt)
                        .dataFetcher { env ->
                            env.getSource<ProjectIndicator>().compliance?.value
                        }
            }
            .field {
                it.name(ProjectIndicator::comment.name)
                        .description("Comment for the indicator")
                        .type(Scalars.GraphQLString)
            }
            .field {
                it.name("annotatedComment")
                        .type(Scalars.GraphQLString)
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
            // Duration since
            .field {
                it.name("durationSecondsSince")
                        .description("Time elapsed (in seconds) since the indicator value was set.")
                        .type(Scalars.GraphQLInt)
                        .dataFetcher { env ->
                            val projectIndicator = env.getSource<ProjectIndicator>()
                            val time = projectIndicator.signature.time
                            (Duration.between(time, Time.now()).toMillis() / 1000).toInt()
                        }
            }
            // Rating
            .field {
                it.name("rating")
                        .description("Rating for this indicator")
                        .type(Scalars.GraphQLString)
                        .dataFetcher { env ->
                            env.getSource<ProjectIndicator>().compliance?.let { compliance ->
                                Rating.asRating(compliance.value)
                            }
                        }
            }
            // OK
            .build()

    private fun annotatedDescription(project: Project, comment: String): String {
        // Gets the list of message annotators to use
        val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(project) }
        // Annotates the message
        return MessageAnnotationUtils.annotate(comment, annotators)
    }

    override fun getTypeName(): String = "${ProjectIndicator::class.java.simpleName}HistoryItem"
}

