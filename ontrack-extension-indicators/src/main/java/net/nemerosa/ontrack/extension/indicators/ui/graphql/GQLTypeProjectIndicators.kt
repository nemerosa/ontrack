package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicators
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicators(
        private val projectCategoryIndicators: GQLTypeProjectCategoryIndicators,
        private val projectIndicator: GQLTypeProjectIndicator,
        private val projectIndicatorService: ProjectIndicatorService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of indicators for a project")
            .field {
                it.name(ProjectIndicators::project.name)
                        .description("Associated project")
                        .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
            }
            .field {
                it.name("indicators")
                        .description("List of indicators")
                        .type(stdList(projectIndicator.typeRef))
                        .argument { arg ->
                            arg.name(ARG_TYPE)
                                    .description("Restriction on the indicator type")
                                    .type(GraphQLString)
                        }
                        .dataFetcher { env ->
                            val project = env.getSource<ProjectIndicators>().project
                            val type: String? = env.getArgument(ARG_TYPE)
                            if (type.isNullOrBlank()) {
                                projectIndicatorService.getProjectIndicators(project.id)
                            } else {
                                listOfNotNull(
                                        projectIndicatorService.findProjectIndicatorByType(project.id, type)
                                )
                            }
                        }
            }
            .field {
                it.name("categories")
                        .description("List of indicator categories")
                        .type(stdList(projectCategoryIndicators.typeRef))
                        .dataFetcher { env ->
                            val project = env.getSource<ProjectIndicators>().project
                            projectIndicatorService.getProjectCategoryIndicators(project.id, true)

                        }
            }
            .build()

    override fun getTypeName(): String = ProjectIndicators::class.java.simpleName

    companion object {
        const val ARG_TYPE = "type"
    }
}