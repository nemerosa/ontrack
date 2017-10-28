package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.checkArgList
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class GQLRootQueryProjects(
        private val structureService: StructureService,
        private val project: GQLTypeProject,
        private val propertyFilter: GQLInputPropertyFilter
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
                .name("projects")
                .type(stdList(project.typeRef))
                .argument(
                        newArgument()
                                .name(ARG_ID)
                                .description("ID of the project to look for")
                                .type(GraphQLInt)
                                .build()
                )
                .argument(
                        newArgument()
                                .name(ARG_NAME)
                                .description("Name of the project to look for")
                                .type(GraphQLString)
                                .build()
                )
                .argument { a ->
                    a.name(ARG_FAVOURITES)
                            .description("Favourite projects only")
                            .type(GraphQLBoolean)
                }
                .argument(propertyFilter.asArgument())
                .dataFetcher(projectFetcher())
                .build()
    }

    private fun projectFetcher(): DataFetcher<List<Project>> {
        return DataFetcher { environment ->
            val id: Int? = environment.getArgument(ARG_ID)
            val name: String? = environment.getArgument(ARG_NAME)
            val favourites = GraphqlUtils.getBooleanArgument(environment, ARG_FAVOURITES, false)
            // Per ID
            when {
                id != null -> {
                    // No other argument is expected
                    checkArgList(environment, ARG_ID)
                    // Fetch by ID
                    val project = structureService.getProject(ID.of(id))
                    // As list
                    return@DataFetcher listOf(project)
                }
                name != null -> {
                    // No other argument is expected
                    checkArgList(environment, ARG_NAME)
                    return@DataFetcher structureService
                            .findProjectByNameIfAuthorized(name)
                            ?.let { listOf(it) }
                            ?: listOf<Project>()
                }
                favourites -> {
                    // No other argument is expected
                    checkArgList(environment, ARG_FAVOURITES)
                    return@DataFetcher structureService.projectFavourites
                }
                else -> {
                    // Filter to use
                    var filter: (Project) -> Boolean = { _ -> true }
                    // Property filter?
                    val propertyFilterArg: Map<String, *>? = environment.getArgument(GQLInputPropertyFilter.ARGUMENT_NAME)
                    if (propertyFilterArg != null) {
                        val filterObject = propertyFilter.convert(propertyFilterArg)
                        if (filterObject != null && StringUtils.isNotBlank(filterObject.type)) {
                            val propertyPredicate = propertyFilter.getFilter(filterObject)
                            filter = filter and { propertyPredicate.test(it) }
                        }
                    }
                    // Whole list
                    return@DataFetcher structureService.projectList.filter(filter)
                }
            }
        }
    }

    companion object {
        @JvmField
        val ARG_ID = "id"
        @JvmField
        val ARG_NAME = "name"
        @JvmField
        val ARG_FAVOURITES = "favourites"
    }
}
