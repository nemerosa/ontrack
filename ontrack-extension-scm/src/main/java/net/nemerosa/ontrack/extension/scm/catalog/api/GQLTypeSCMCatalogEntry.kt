package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.GQLScalarLocalDateTime
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCatalogEntry(
        private val catalogLinkService: CatalogLinkService,
        private val scmCatalogTeam: GQLTypeSCMCatalogTeam
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
                        it.name("repositoryPage")
                                .description("URL to browse the repository")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("lastActivity")
                                .description("Last activity timestamp")
                                .type(GQLScalarLocalDateTime.INSTANCE)
                    }
                    .field {
                        it.name("createdAt")
                                .description("Creation timestamp")
                                .type(GQLScalarLocalDateTime.INSTANCE)
                    }
                    .field {
                        it.name("timestamp")
                                .description("Collection timestamp")
                                .type(GQLScalarLocalDateTime.INSTANCE)
                    }
                    .field {
                        it.name("linked")
                                .description("Flag to indicate if this SCM catalog entry is linked to a project")
                                .type(GraphQLBoolean)
                                .dataFetcher { env ->
                                    val entry = env.getSource<SCMCatalogEntry>()!!
                                    catalogLinkService.isLinked(entry)
                                }
                    }
                    .field {
                        it.name("project")
                                .description("Project linked to this SCM catalog entry. Might be null")
                                .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
                                .dataFetcher { env ->
                                    val entry = env.getSource<SCMCatalogEntry>()
                                    catalogLinkService.getLinkedProject(entry)!!
                                }
                    }
                    .field {
                        it.name(SCMCatalogEntry::teams.name)
                            .description("List of teams this entry belongs to")
                            .type(GraphQLList(GraphQLNonNull(scmCatalogTeam.typeRef)))
                    }
                    .build()

    override fun getTypeName(): String = SCM_CATALOG_ENTRY

    companion object {
        val SCM_CATALOG_ENTRY: String = SCMCatalogEntry::class.java.simpleName
    }
}