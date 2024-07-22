package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningBranchTrail
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningBranchTrail : GQLType {

    override fun getTypeName(): String = AutoVersioningBranchTrail::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(AutoVersioningBranchTrail::class))
            .stringField(AutoVersioningBranchTrail::id)
            .field(AutoVersioningBranchTrail::branch)
            .field(AutoVersioningBranchTrail::configuration)
            .stringField(AutoVersioningBranchTrail::rejectionReason)
            .stringField(AutoVersioningBranchTrail::orderId)
            // TODO Link to the AV order
            .build()
}