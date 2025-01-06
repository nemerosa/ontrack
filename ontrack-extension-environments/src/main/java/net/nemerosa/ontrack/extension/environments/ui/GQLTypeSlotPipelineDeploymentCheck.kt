package net.nemerosa.ontrack.extension.environments.ui

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotPipelineDeploymentCheck
import net.nemerosa.ontrack.extension.environments.security.SlotPipelineOverride
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanFieldFunction
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineDeploymentCheck(
    private val securityService: SecurityService,
) : GQLType {

    override fun getTypeName(): String = SlotPipelineDeploymentCheck::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of checks for admission for a pipeline")
            .field(SlotPipelineDeploymentCheck::check)
            .field(SlotPipelineDeploymentCheck::config)
            .jsonField(SlotPipelineDeploymentCheck::ruleData)
            .field(SlotPipelineDeploymentCheck::override)
            .booleanFieldFunction<SlotPipelineDeploymentCheck>(
                "overridden",
                "Flag to check if the rule has been overridden"
            ) {
                it.override != null
            }
            .field {
                it.name("canBeOverridden")
                    .description("True if the user is allowed to override this rule")
                    .type(GraphQLBoolean)
                    .dataFetcher { env ->
                        val check: SlotPipelineDeploymentCheck = env.getSource()
                        securityService.isProjectFunctionGranted(
                            check.config.slot.project,
                            SlotPipelineOverride::class.java
                        )
                    }
            }
            .build()
}