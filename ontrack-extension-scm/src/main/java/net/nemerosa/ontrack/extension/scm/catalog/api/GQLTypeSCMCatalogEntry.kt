package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarLocalDateTime
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCatalogEntry(
        private val catalogInfo: GQLTypeCatalogInfo,
        private val catalogLinkService: CatalogLinkService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("SCM Catalog entry")
                    .field {
                        it.name("scm")
                                .description("Type of SCM")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("config")
                                .description("SCM Config name")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("repository")
                                .description("SCM repository location")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("timestamp")
                                .description("Collection timestamp")
                                .type(GQLScalarLocalDateTime.INSTANCE)
                    }
                    .field {
                        it.name("linked")
                                .description("Is this repository linked to an actual location?")
                                .type(GraphQLBoolean)
                    }
                    .field {
                        it.name("link")
                                .description("Link to Ontrack project and SCM information")
                                .type(catalogInfo.typeRef)
                                .dataFetcher { env -> loadCatalogInfo(env) }
                    }
                    .build()

    override fun getTypeName(): String = SCM_CATALOG_ENTRY

    private fun loadCatalogInfo(env: DataFetchingEnvironment): GQLTypeCatalogInfo.Data? {
        val entry: SCMCatalogEntry = env.getSource()
        val project = catalogLinkService.getLinkedProject(entry)
        return project?.run {
            GQLTypeCatalogInfo.Data(
                    project = project
            )
        }
    }

    companion object {
        val SCM_CATALOG_ENTRY: String = SCMCatalogEntry::class.java.simpleName
    }
}