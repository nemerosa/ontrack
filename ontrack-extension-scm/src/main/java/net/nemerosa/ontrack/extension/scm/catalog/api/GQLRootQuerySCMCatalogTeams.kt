package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogTeam
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySCMCatalogTeams(
    private val gqlTypeSCMCatalogTeam: GQLTypeSCMCatalogTeam,
    private val scmCatalog: SCMCatalog,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("scmCatalogTeams")
        .description("List of teams indexed by the SCM catalog")
        .type(listType(gqlTypeSCMCatalogTeam.typeRef))
        .dataFetcher { _ ->
            // Collecting teams individually
            val teams = mutableMapOf<String, SCMCatalogTeam>()
            scmCatalog.catalogEntries.forEach { entry ->
                entry.teams?.forEach { team ->
                    if (!teams.containsKey(team.id)) {
                        teams[team.id] = team
                    }
                }
            }
            // OK
            teams.values.toList()
        }
        .build()

}