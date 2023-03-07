package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildAutoVersioning : GQLType {

    override fun getTypeName(): String = "BuildAutoVersioning"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Gathering information about the auto versioning around this build")
            .field(Context::config, AutoVersioningSourceConfig::class.java.simpleName)
            .build()

    data class Context(
        val parentBranch: Branch,
        val dependencyBuild: Build,
        @APIDescription("AV source config")
        val config: AutoVersioningSourceConfig,
    )
}