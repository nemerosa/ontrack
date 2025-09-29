package net.nemerosa.ontrack.extension.issues.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.issues.IssueServiceRegistry
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIssueServiceConfigurations(
    private val issueServiceConfigurationRepresentationGQLType: IssueServiceConfigurationRepresentationGQLType,
    private val issueServiceRegistry: IssueServiceRegistry,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("issueServiceConfigurations")
            .description("List of issue services")
            .type(listType(issueServiceConfigurationRepresentationGQLType.typeRef))
            .dataFetcher {
                issueServiceRegistry.issueServices.flatMap { service ->
                    service.getConfigurationList().map { config ->
                        IssueServiceConfigurationRepresentation.of(
                            issueServiceExtension = service,
                            issueServiceConfiguration = config,
                        )
                    }
                }
            }
            .build()

}