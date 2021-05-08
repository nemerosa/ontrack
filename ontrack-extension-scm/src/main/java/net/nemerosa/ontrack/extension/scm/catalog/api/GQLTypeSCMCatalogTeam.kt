package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogTeam
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.GQLScalarLocalDateTime
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCatalogTeam : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("SCM Catalog team")
            .stringField(SCMCatalogTeam::id, "Team ID in the SCM")
            .stringField(SCMCatalogTeam::name, "Team name")
            .stringField(SCMCatalogTeam::description, "Team description")
            .stringField(SCMCatalogTeam::url, "Team URL")
            .stringField(SCMCatalogTeam::role, "Team role in the SCM entry")
            .build()

    override fun getTypeName(): String = SCM_CATALOG_TEAM

    companion object {
        val SCM_CATALOG_TEAM: String = SCMCatalogTeam::class.java.simpleName
    }
}