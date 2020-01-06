package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySCMCatalog(
        private val paginatedListFactory: GQLPaginatedListFactory,
        private val scmCatalogEntry: GQLTypeSCMCatalogEntry,
        private val catalogLinkService: CatalogLinkService,
        private val scmCatalog: SCMCatalog
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
                            GraphQLArgument.newArgument().name("linked")
                                    .description("Filters on entries which are linked or not to projects (ALL, LINKED, ORPHAN)")
                                    .type(GraphQLString)
                                    .build()
                    )
            )

    private fun loadSCMCatalogEntries(env: DataFetchingEnvironment, offset: Int, size: Int): List<SCMCatalogEntry> {
        val scm: String? = env.getArgument("scm")
        val config: String? = env.getArgument("config")
        val repository: Regex? = env.getArgument<String>("repository")?.toRegex()
        val linked: CatalogEntryLink = env.getArgument<String>("linked")?.run { CatalogEntryLink.valueOf(this) }
                ?: CatalogEntryLink.ALL
        return scmCatalog.catalogEntries.filter {
            (scm.isNullOrBlank() || it.scm == scm) &&
                    (config.isNullOrBlank() || it.config == config) &&
                    (repository == null || repository.matches(it.repository)) &&
                    (when (linked) {
                        CatalogEntryLink.ALL -> true
                        CatalogEntryLink.LINKED -> isLinked(it)
                        CatalogEntryLink.ORPHAN -> !isLinked(it)
                    })
        }.toList().drop(offset).take(size)
    }

    private fun isLinked(entry: SCMCatalogEntry): Boolean = catalogLinkService.getLinkedProject(entry) != null
}