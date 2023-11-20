package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.BranchLink
import org.springframework.stereotype.Component

@Component
class GQLTypeBranchLink(
    private val extensionManager: ExtensionManager,
) : GQLType {

    private val extensions: Collection<GQLTypeBranchLinkExtension> by lazy {
        extensionManager.getExtensions(GQLTypeBranchLinkExtension::class.java)
    }

    override fun getTypeName(): String = BranchLink::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Qualified link to a branch")
            .field(BranchLink::branch, GQLTypeBranch.BRANCH)
            .stringField(BranchLink::qualifier)
            .field(BranchLink::sourceBuild, GQLTypeBuild.BUILD)
            .field(BranchLink::targetBuild, GQLTypeBuild.BUILD)
            .fields(
                extensions.flatMap {
                    it.additionalFields
                }
            )
            .build()

}