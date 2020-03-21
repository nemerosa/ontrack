package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntryOrProject
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMCatalogProjectEntry(
        private val scmCatalogEntry: GQLTypeSCMCatalogEntry
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("SCM Catalog entry or/and project")
                    .field {
                        it.name("entry")
                                .description("SCM Catalog entry")
                                .type(scmCatalogEntry.typeRef)
                    }
                    .field {
                        it.name("project")
                                .description("Associated project or orphan project")
                                .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
                    }
                    .build()

    override fun getTypeName(): String = SCM_CATALOG_ENTRY_OR_PROJECT

    companion object {
        val SCM_CATALOG_ENTRY_OR_PROJECT: String = SCMCatalogEntryOrProject::class.java.simpleName
    }
}