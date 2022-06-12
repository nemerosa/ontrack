package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLInputAutoVersioningAuditQueryFilter : GQLInputType<AutoVersioningAuditQueryFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLInputObjectType.newInputObject()
            .name(AutoVersioningAuditQueryFilter::class.java.simpleName)
            .description("Filter when querying auto versioning processing orders")
            .field {
                it.name(AutoVersioningAuditQueryFilter::project.name)
                    .description("Name of the target project")
                    .type(GraphQLString)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::branch.name)
                    .description("Name of the target branch")
                    .type(GraphQLString)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::uuid.name)
                    .description("UUID of the auto versioning processing order")
                    .type(GraphQLString)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::state.name)
                    .description("Most recent state of the auto versioning processing order")
                    .type(GraphQLString)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::running.name)
                    .description("Running state of the auto versioning processing order")
                    .type(GraphQLBoolean)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::source.name)
                    .description("Name of the source project of the auto versioning processing order")
                    .type(GraphQLString)
            }
            .field {
                it.name(AutoVersioningAuditQueryFilter::version.name)
                    .description("Target version of the auto versioning processing order")
                    .type(GraphQLString)
            }
            .build()

    override fun convert(argument: Any?): AutoVersioningAuditQueryFilter =
        argument?.asJson()?.parse() ?: AutoVersioningAuditQueryFilter()

    override fun getTypeRef() = GraphQLTypeReference(AutoVersioningAuditQueryFilter::class.java.simpleName)
}