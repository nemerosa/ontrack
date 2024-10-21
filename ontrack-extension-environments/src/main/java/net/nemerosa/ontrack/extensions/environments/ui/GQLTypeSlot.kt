package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.SlotPipeline
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.authorizations.GQLInterfaceAuthorizableService
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringField
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
) : GQLType {
    override fun getTypeName(): String = Slot::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Deployment slot into an environment")
            .stringField(Slot::id)
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
                    .description("Current pipeline active in the slot")
                    .type(gqlTypeSlotPipeline.typeRef)
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getCurrentPipeline(slot)
                    }
            }
            // Paginated list of pipelines
            .field(
                paginatedListFactory.createPaginatedField<Slot, SlotPipeline>(
                    cache = cache,
                    fieldName = "pipelines",
                    fieldDescription = "Paginated list of pipelines",
                    itemType = gqlTypeSlotPipeline.typeName,
                    // TODO Filtering the pipelines
                    itemPaginatedListProvider = { _, slot, _, _ ->
                        // TODO Pagination of pipelines
                        slotService.findPipelines(slot)
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
            // OK
            .build()
}