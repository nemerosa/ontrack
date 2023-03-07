package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildAutoVersioning(
    private val gqlTypeAutoVersioningAuditEntry: GQLTypeAutoVersioningAuditEntry,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
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
            // OK
            .build()

    private fun getLastAuditEntry(context: Context): AutoVersioningAuditEntry? =
        autoVersioningAuditQueryService.findByFilter(
            AutoVersioningAuditQueryFilter(
                project = context.parentBranch.project.name,
                branch = context.parentBranch.name,
                source = context.dependencyBuild.project.name,
                count = 1
            )
        ).firstOrNull()

    data class Context(
        val parentBranch: Branch,
        val dependencyBuild: Build,
        @APIDescription("AV source config")
        val config: AutoVersioningSourceConfig,
    )
}