package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.catalog.*
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.graphql.support.toTypeRef
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCatalogTeam(
    private val scmCatalogFilterService: SCMCatalogFilterService,
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("SCM Catalog team")
            .stringField(SCMCatalogTeam::id, "Team ID in the SCM")
            .stringField(SCMCatalogTeam::name, "Team name")
            .stringField(SCMCatalogTeam::description, "Team description")
            .stringField(SCMCatalogTeam::url, "Team URL")
            .stringField(SCMCatalogTeam::role, "Team role in the SCM entry")
            // List of entries for this team
            .field {
                it.name("entries")
                    .description("List of SCM catalog entries for this team")
                    .type(listType(SCMCatalogEntry::class.toTypeRef()))
                    .dataFetcher { env ->
                        val team: SCMCatalogTeam = env.getSource()
                        scmCatalogFilterService.findCatalogProjectEntries(
                            SCMCatalogProjectFilter(size = Int.MAX_VALUE, team = team.id)
                        ).mapNotNull(SCMCatalogEntryOrProject::entry)
                    }
            }
            // Number of entries for this team
            .field {
                it.name("entryCount")
                    .description("Number of SCM catalog entries for this team")
                    .type(GraphQLInt.toNotNull())
                    .dataFetcher { env ->
                        val team: SCMCatalogTeam = env.getSource()
                        scmCatalogFilterService.findCatalogProjectEntries(
                            SCMCatalogProjectFilter(size = Int.MAX_VALUE, team = team.id)
                        ).mapNotNull(SCMCatalogEntryOrProject::entry).size
                    }
            }
            // OK
            .build()

    override fun getTypeName(): String = SCM_CATALOG_TEAM

    companion object {
        val SCM_CATALOG_TEAM: String = SCMCatalogTeam::class.java.simpleName
    }
}