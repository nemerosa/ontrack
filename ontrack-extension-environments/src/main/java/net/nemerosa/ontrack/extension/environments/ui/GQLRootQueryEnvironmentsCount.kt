package net.nemerosa.ontrack.extension.environments.ui

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryEnvironmentsCount(
    private val securityService: SecurityService,
    private val environmentService: EnvironmentService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("environmentsCount")
            .description("Returns the number of environments")
            .type(GraphQLInt.toNotNull())
            .dataFetcher {
                securityService.asAdmin {
                    environmentService.findAll().size
                }
            }
            .build()

}