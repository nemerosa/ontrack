package net.nemerosa.ontrack.extension.workflows.mock

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC, RunProfile.UNIT_TEST, RunProfile.DEV)
class GQLRootQueryMockWorkflowTexts(
    private val mockWorkflowNodeExecutor: MockWorkflowNodeExecutor,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("mockWorkflowTexts")
            .description("Getting a list of texts generated for a mock workflow")
            .type(listType(GraphQLString))
            .argument(stringArgument("instanceId", "ID of the workflow instance"))
            .dataFetcher { env ->
                val instanceId: String = env.getArgument("instanceId")
                mockWorkflowNodeExecutor.getTextsByInstanceId(instanceId)
            }
            .build()
}