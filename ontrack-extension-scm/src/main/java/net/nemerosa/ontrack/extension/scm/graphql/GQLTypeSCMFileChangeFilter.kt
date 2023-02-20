package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.stringListField
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMFileChangeFilter : GQLType {

    override fun getTypeName(): String = SCMFileChangeFilter::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Saved file change filter")
            .stringField(SCMFileChangeFilter::name, "Name of the filter")
            .stringListField(SCMFileChangeFilter::patterns, "List of ANT-like patterns for the paths")
            .build()
}