package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQuerySCMCatalogTeamStats(
    private val scmCatalog: SCMCatalog,
    private val gqlTypeSCMCatalogTeamStats: GQLTypeSCMCatalogTeamStats
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("scmCatalogTeamStats")
        .description("Counts of SCM catalog entries having N teams")
        .type(listType(gqlTypeSCMCatalogTeamStats.typeRef))
        .dataFetcher {
            // Collecting team counts
            val teamCounts = mutableMapOf<Int, Int>()
            scmCatalog.catalogEntries.forEach { entry ->
                val teamCount = entry.teams?.size ?: 0
                val currentCount = teamCounts[teamCount]
                if (currentCount != null) {
                    teamCounts[teamCount] = currentCount + 1
                } else {
                    teamCounts[teamCount] = 1
                }
            }
            // OK
            teamCounts.map { (teamCount, entryCount) ->
                SCMCatalogTeamStats(teamCount, entryCount)
            }.sortedByDescending { it.teamCount }
        }
        .build()

}

@Component
class GQLTypeSCMCatalogTeamStats : GQLType {

    override fun getTypeName(): String = SCMCatalogTeamStats::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Number of entries in the SCM catalog having this number of teams")
        .intField(SCMCatalogTeamStats::teamCount, "Number of teams")
        .intField(SCMCatalogTeamStats::entryCount, "Number of entries having this number of teams")
        .build()
}

data class SCMCatalogTeamStats(
    val teamCount: Int,
    val entryCount: Int,
)
