package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflow
import net.nemerosa.ontrack.extension.environments.workflows.SlotWorkflowService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.authorizations.GQLInterfaceAuthorizableService
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GQLTypeSlot(
    private val slotService: SlotService,
    private val gqlTypeSlotPipeline: GQLTypeSlotPipeline,
    private val gqlTypeSlotAdmissionRuleConfig: GQLTypeSlotAdmissionRuleConfig,
    private val gqlInterfaceAuthorizableService: GQLInterfaceAuthorizableService,
    private val paginatedListFactory: GQLPaginatedListFactory,
    private val slotWorkflowService: SlotWorkflowService,
) : GQLType {
    override fun getTypeName(): String = Slot::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Deployment slot into an environment")
            .idFieldForString(Slot::id)
            .stringField(Slot::description)
            .field(Slot::project)
            .stringField(Slot::qualifier)
            .field(Slot::environment)
            // Authorizations
            .apply {
                gqlInterfaceAuthorizableService.apply(this, Slot::class)
            }
            // Last eligible build
            .field {
                it.name("eligibleBuild")
                    .description("Last eligible build for this slot")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getEligibleBuilds(slot, count = 1).firstOrNull()
                    }
            }
            // Paginated list of eligible builds
            .field(
                paginatedListFactory.createPaginatedField<Slot, Build>(
                    cache = cache,
                    fieldName = "eligibleBuilds",
                    fieldDescription = "Paginated list of eligible builds",
                    itemType = GQLTypeBuild.BUILD,
                    itemPaginatedListProvider = { _, slot, _, size ->
                        // TODO Pagination of eligible builds
                        PaginatedList.create(
                            slotService.getEligibleBuilds(slot, count = size),
                            offset = 0,
                            pageSize = size
                        )
                    }
                )
            )
            // Current pipeline
            .field {
                it.name("currentPipeline")
                    .description("Current pipeline in the slot")
                    .type(gqlTypeSlotPipeline.typeRef)
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getCurrentPipeline(slot)
                    }
            }
            // Last deployed pipeline
            .field {
                it.name("lastDeployedPipeline")
                    .description("Last deployed pipeline in the slot")
                    .type(gqlTypeSlotPipeline.typeRef)
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getLastDeployedPipeline(slot)
                    }
            }
            // Paginated list of pipelines
            .field(
                paginatedListFactory.createPaginatedField<Slot, SlotPipeline>(
                    cache = cache,
                    fieldName = "pipelines",
                    fieldDescription = "Paginated list of pipelines",
                    itemType = gqlTypeSlotPipeline.typeName,
                    itemPaginatedListProvider = { _, slot, offset, size ->
                        slotService.findPipelines(slot, offset, size)
                    }
                )
            )
            // List of rules
            .field {
                it.name("admissionRules")
                    .description("List of configured admission rules for this slot")
                    .type(listType(gqlTypeSlotAdmissionRuleConfig.typeRef))
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getAdmissionRuleConfigs(slot)
                    }
            }
            // List of workflows
            .listFieldGetter<Slot, SlotWorkflow>("workflows", "List of workflows for this slot") {
                slotWorkflowService.getSlotWorkflowsBySlot(it)
            }
            // OK
            .build()
}