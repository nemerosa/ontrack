package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilter
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterLink
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySCMCatalog(
        private val paginatedListFactory: GQLPaginatedListFactory,
        private val scmCatalogEntry: GQLTypeSCMCatalogEntry,
        private val scmCatalogFilterService: SCMCatalogFilterService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            paginatedListFactory.createPaginatedField<Any?, SCMCatalogEntry>(
                    cache = GQLTypeCache(),
                    fieldName = "scmCatalog",
                    fieldDescription = "List of SCM catalog entries",
                    itemType = scmCatalogEntry,
                    itemListCounter = { env, _ -> loadSCMCatalogEntries(env, 0, Int.MAX_VALUE).size },
                    itemListProvider = { env, _, offset, size -> loadSCMCatalogEntries(env, offset, size) },
                    arguments = listOf(
                            GraphQLArgument.newArgument().name("scm")
                                    .description("Filters on SCM type (exact match)")
                                    .type(GraphQLString)
                                    .build(),
                            GraphQLArgument.newArgument().name("config")
                                    .description("Filters on SCM config name (exact match)")
                                    .type(GraphQLString)
                                    .build(),
                            GraphQLArgument.newArgument().name("repository")
                                    .description("Filters on repository (regular expression)")
                                    .type(GraphQLString)
                                    .build(),
                            GraphQLArgument.newArgument().name("link")
                                    .description("Filters on entries which are linked or not to projects (ALL, LINKED, ORPHAN)")
                                    .type(GraphQLString)
                                    .build()
                    )
            )

    private fun loadSCMCatalogEntries(env: DataFetchingEnvironment, offset: Int, size: Int): List<SCMCatalogEntry> {
        val scm: String? = env.getArgument("scm")
        val config: String? = env.getArgument("config")
        val repository: String? = env.getArgument<String>("repository")
        val link: SCMCatalogFilterLink = env.getArgument<String>("link")?.run { SCMCatalogFilterLink.valueOf(this) }
                ?: SCMCatalogFilterLink.ALL
        val filter = SCMCatalogFilter(
                offset = offset,
                size = size,
                scm = scm,
                config = config,
                repository = repository,
                link = link
        )
        return scmCatalogFilterService.findCatalogEntries(filter)
    }

}