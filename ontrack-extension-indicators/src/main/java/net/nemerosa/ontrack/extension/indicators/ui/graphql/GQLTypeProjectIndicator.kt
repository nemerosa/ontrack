package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.indicators.model.Rating
import net.nemerosa.ontrack.extension.indicators.stats.trendBetween
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class GQLTypeProjectIndicator(
        private val projectIndicatorType: GQLTypeProjectIndicatorType,
        private val signature: GQLTypeCreation,
        private val paginatedListFactory: GQLPaginatedListFactory,
        private val projectIndicatorHistoryItem: GQLTypeProjectIndicatorHistoryItem,
        private val fieldContributors: List<GQLFieldContributor>,
        private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
        private val projectIndicatorService: ProjectIndicatorService
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
            // Duration since
            .field {
                it.name("durationSecondsSince")
                        .description("Time elapsed (in seconds) since the indicator value was set.")
                        .type(GraphQLInt)
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
                        .type(GraphQLString)
                        .dataFetcher { env ->
                            env.getSource<ProjectIndicator>().compliance?.let { compliance ->
                                Rating.asRating(compliance.value)
                            }
                        }
            }
            // Previous indicator value
            .field {
                it.name("previousValue")
                        .description("Previous value for this indicator")
                        .type(GraphQLTypeReference(typeName))
                        .dataFetcher { env ->
                            val projectIndicator = env.getSource<ProjectIndicator>()
                            projectIndicatorService.getPreviousIndicator(projectIndicator)
                        }
            }
            // Previous value trend
            .field {
                it.name("trendSincePrevious")
                        .description("Trend since the previous value (if any)")
                        .type(GraphQLString)
                        .dataFetcher { env ->
                            val projectIndicator = env.getSource<ProjectIndicator>()
                            val previousIndicator = projectIndicatorService.getPreviousIndicator(projectIndicator)
                            trendBetween(
                                    previousIndicator.compliance,
                                    projectIndicator.compliance
                            )
                        }
            }
            // History of this indicator
            .field(
                    paginatedListFactory.createPaginatedField<ProjectIndicator, ProjectIndicator>(
                            cache = cache,
                            fieldName = "history",
                            fieldDescription = "History of this indicator",
                            itemType = projectIndicatorHistoryItem,
                            itemPaginatedListProvider = { env, source, offset, size ->
                                history(source, offset, size)
                            }
                    )
            )
            // Links
            .fields(ProjectIndicator::class.java.graphQLFieldContributions(fieldContributors))
            .build()

    private fun history(projectIndicator: ProjectIndicator, offset: Int, size: Int): PaginatedList<ProjectIndicator> {
        val results = projectIndicatorService.getHistory(projectIndicator, offset, size)
        return PaginatedList.create(
                items = results.items,
                offset = offset,
                pageSize = size,
                total = results.total
        )
    }

    private fun annotatedDescription(project: Project, comment: String): String {
        // Gets the list of message annotators to use
        val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(project) }
        // Annotates the message
        return MessageAnnotationUtils.annotate(comment, annotators)
    }

    override fun getTypeName(): String = ProjectIndicator::class.java.simpleName
}