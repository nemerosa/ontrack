package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.SlotDeploymentCheck
import net.nemerosa.ontrack.extension.environments.SlotPipelineAdmissionRuleStatus
import net.nemerosa.ontrack.extension.environments.security.SlotPipelineOverride
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanFieldFunction
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLTypeSlotPipelineAdmissionRuleStatus(
    private val securityService: SecurityService,
    private val slotService: SlotService,
) : GQLType {
    override fun getTypeName(): String = SlotPipelineAdmissionRuleStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Stored state for an admission rule in a pipeline")
            .field(SlotPipelineAdmissionRuleStatus::admissionRuleConfig)
            .field(SlotPipelineAdmissionRuleStatus::data)
            .field(SlotPipelineAdmissionRuleStatus::override)
            .field {
                it.name("check")
                    .description("Status check for this rule")
                    .type(SlotDeploymentCheck::class.toTypeRef().toNotNull())
                    .dataFetcher { env ->
                        val ruleStatus: SlotPipelineAdmissionRuleStatus = env.getSource()!!
                        slotService.getAdmissionRuleCheck(
                            ruleStatus = ruleStatus,
                        )
                    }
            }
            .booleanFieldFunction<SlotPipelineAdmissionRuleStatus>(
                "overridden",
                "Flag to check if the rule has been overridden"
            ) {
                it.override != null
            }
            .booleanFieldFunction<SlotPipelineAdmissionRuleStatus>(
                "canBeOverridden",
                "True if the user is allowed to override this rule"
            ) {
                securityService.isProjectFunctionGranted(
                    it.pipeline.slot.project,
                    SlotPipelineOverride::class.java
                )
            }
            .build()
}