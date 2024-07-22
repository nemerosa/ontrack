package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.listField
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningConfiguredBranch : GQLType {

    override fun getTypeName(): String = AutoVersioningConfiguredBranch::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(AutoVersioningConfiguredBranch::class))
            .field(AutoVersioningConfiguredBranch::branch)
            .field(AutoVersioningConfiguredBranch::configuration)
            .build()
}