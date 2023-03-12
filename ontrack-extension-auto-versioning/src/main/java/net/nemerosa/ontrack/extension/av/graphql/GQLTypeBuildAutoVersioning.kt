package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildAutoVersioning(
    private val gqlTypeAutoVersioningAuditEntry: GQLTypeAutoVersioningAuditEntry,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
    private val buildFilterService: BuildFilterService,
    private val autoVersioningConfigurationService: AutoVersioningConfigurationService,
) : GQLType {

    override fun getTypeName(): String = "BuildAutoVersioning"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Gathering information about the auto versioning around this build")
            .field(Context::config, AutoVersioningSourceConfig::class.java.simpleName)
            // Last AV status
            .field {
                it.name("status")
                    .description("Last auto versioning entry for this dependency.")
                    .type(gqlTypeAutoVersioningAuditEntry.typeRef)
                    .dataFetcher { env ->
                        val context: Context = env.getSource()
                        getLastAuditEntry(context)
                    }
            }
            // Last eligible build for this AV config
            .field {
                it.name("lastEligibleBuild")
                    .description("Last eligible build for this AV config")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val context: Context = env.getSource()
                        getLastEligibleBuild(context)
                    }
            }
            // OK
            .build()

    private fun getLastEligibleBuild(context: Context): Build? {
        val av = context.config
        val dependencyBranch = context.dependencyBranch
        // Gets the latest branch
        val latestBranch = autoVersioningConfigurationService.getLatestBranch(dependencyBranch.project, av)
            ?: return null
        // Gets the latest promoted build on this branch
        return buildFilterService.standardFilterProviderData(1)
            .withWithPromotionLevel(av.sourcePromotion)
            .build()
            .filterBranchBuilds(latestBranch)
            .firstOrNull()
    }

    private fun getLastAuditEntry(context: Context): AutoVersioningAuditEntry? =
        autoVersioningAuditQueryService.findByFilter(
            AutoVersioningAuditQueryFilter(
                project = context.parentBranch.project.name,
                branch = context.parentBranch.name,
                source = context.dependencyBranch.project.name,
                count = 1
            )
        ).firstOrNull()

    data class Context(
        val parentBranch: Branch,
        val dependencyBranch: Branch,
        @APIDescription("AV source config")
        val config: AutoVersioningSourceConfig,
    )
}