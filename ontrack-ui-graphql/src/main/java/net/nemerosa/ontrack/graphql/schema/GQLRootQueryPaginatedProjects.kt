package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPaginatedProjects(
    private val structureService: StructureService,
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val project: GQLTypeProject,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createRootPaginatedField(
            cache = GQLTypeCache(),
            fieldName = "paginatedProjects",
            fieldDescription = "Paginated list of projects",
            itemType = project.typeName,
            arguments = listOf(
                stringArgument("name", "Fragment of the project name to filter on")
            ),
            itemPaginatedListProvider = { env, offset, size ->
                val name: String? = env.getArgument("name")
                val items = if (name.isNullOrBlank()) {
                    structureService.projectList
                } else {
                    structureService.findProjectsByNamePattern(name)
                }
                PaginatedList.create(
                    items = items,
                    offset = offset,
                    pageSize = size,
                )
            }
        )
}