package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.model.extension.Extension

/**
 * Contributing additional fields to the `BranchLink` type.
 */
interface GQLTypeBranchLinkExtension: Extension {

    val additionalFields: List<GraphQLFieldDefinition>

}