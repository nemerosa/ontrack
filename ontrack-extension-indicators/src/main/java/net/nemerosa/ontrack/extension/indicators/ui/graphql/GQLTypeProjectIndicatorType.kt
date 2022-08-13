package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.model.Rating
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicatorType(
    private val indicatorTypeService: IndicatorTypeService,
    private val indicatorValueType: GQLTypeIndicatorValueType,
    private val indicatorReportingService: GQLIndicatorReportingService,
    private val indicatorService: IndicatorService,
    private val indicatorSource: GQLTypeIndicatorSource,
    private val fieldContributors: List<GQLFieldContributor>,
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Type of indicator")
        .stringField(ProjectIndicatorType::id.name, "Unique ID for the type")
        .stringField(ProjectIndicatorType::name.name, "Name for the indicator type")
        .stringField(ProjectIndicatorType::deprecated.name, "Indicator type deprecation reason if any")
        .stringField(ProjectIndicatorType::link.name, "Link to the definition of the indicator")
        // Computed flag
        .booleanField(ProjectIndicatorType::computed)
        // Source
        .field {
            it.name(IndicatorType<*, *>::source.name)
                .description("Source for this type")
                .type(indicatorSource.typeRef)
                .dataFetcher { env ->
                    val typeRef = env.getSource<ProjectIndicatorType>()
                    val type = indicatorTypeService.getTypeById(typeRef.id)
                    type.source
                }
        }
        // Category
        .field {
            it.name(ProjectIndicatorType::category.name)
                .description("Associated category")
                .type(GraphQLTypeReference(GQLTypeIndicatorCategory.INDICATOR_CATEGORY))
        }
        // Value type
        .field {
            it.name(IndicatorType<*, *>::valueType.name)
                .description("Value type")
                .type(indicatorValueType.typeRef)
                .dataFetcher { env ->
                    val typeRef = env.getSource<ProjectIndicatorType>()
                    val type = indicatorTypeService.getTypeById(typeRef.id)
                    type.valueType
                }
        }
        // Value config
        .field {
            it.name(IndicatorType<*, *>::valueConfig.name)
                .description("Configuration for the value type")
                .type(GQLScalarJSON.INSTANCE)
                .dataFetcher { env ->
                    val typeRef = env.getSource<ProjectIndicatorType>()
                    val type = indicatorTypeService.getTypeById(typeRef.id)
                    type.toConfigClientJson()
                }
        }
        // Report of values for projects
        .field {
            it.name("indicators")
                .description("List of indicators for this type")
                .type(listType(ProjectIndicator::class.toTypeRef()))
                .arguments(indicatorReportingService.arguments)
                .rateArgument()
                .dataFetcher { env ->
                    val typeRef = env.getSource<ProjectIndicatorType>()
                    val type = indicatorTypeService.getTypeById(typeRef.id)
                    indicators(type, env)
                }
        }
        // Links
        .fields(ProjectIndicatorType::class.java.graphQLFieldContributions(fieldContributors))
        // OK
        .build()

    private fun indicators(type: IndicatorType<*, *>, env: DataFetchingEnvironment): List<ProjectIndicator> {
        val projects = indicatorReportingService.findProjects(env, listOf(type))
        val rate = env.getRateArgument()
        return projects.map { project ->
            ProjectIndicator(
                project = project,
                indicator = indicatorService.getProjectIndicator(project, type)
            )
        }.filter { projectIndicator ->
            if (rate != null) {
                projectIndicator.compliance?.let { compliance ->
                    Rating.asRating(compliance.value) <= rate
                } ?: false
            } else {
                true
            }
        }
    }

    override fun getTypeName(): String = ProjectIndicatorType::class.java.simpleName

}