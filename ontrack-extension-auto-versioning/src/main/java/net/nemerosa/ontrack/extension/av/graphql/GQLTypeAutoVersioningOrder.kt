package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBranch
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningOrder(
    private val gqlEnumAutoApprovalMode: GQLEnumAutoApprovalMode,
) : GQLType {

    override fun getTypeName(): String = AutoVersioningOrder::class.java.simpleName

    override fun createType(cache: GQLTypeCache?): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Auto versioning processing order")
            .stringField(AutoVersioningOrder::uuid, "UUID for the processing order")
            .stringField(AutoVersioningOrder::sourceProject, "Source project for the processing order")
            .field {
                it.name("branch")
                    .description("Target branch for the processing order")
                    .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
            }
            .field {
                it.name("targetPaths")
                    .description("List of paths to update with the target version")
                    .type(GraphQLList(Scalars.GraphQLString))
            }
            .stringField(
                AutoVersioningOrder::targetRegex,
                "Regex used to identify the line to update in the target files"
            )
            .stringField(AutoVersioningOrder::targetProperty, "Property in the target file")
            .stringField(
                AutoVersioningOrder::targetPropertyRegex,
                "Regex used in property value to identify the version"
            )
            .stringField(AutoVersioningOrder::targetPropertyType, "Type of the target file")
            .stringField(AutoVersioningOrder::targetVersion, "Version to set")
            .booleanField(AutoVersioningOrder::autoApproval, "If the PR must be auto approved & merged")
            .stringField(AutoVersioningOrder::upgradeBranchPattern, "Pattern for the branch being used for the update")
            .stringField(AutoVersioningOrder::postProcessing, "Post processing type")
            .field {
                it.name("postProcessingConfig")
                    .description("JSON configuration for the post processing")
                    .type(GQLScalarJSON.INSTANCE)
            }
            .stringField(
                AutoVersioningOrder::validationStamp,
                "Ontrack validation stamp associated with this auto versioning (used for checks)"
            )
            .enumField(
                AutoVersioningOrder::autoApprovalMode,
                "Defines the way the PR is merged when auto approval is set."
            )
            .build()
}