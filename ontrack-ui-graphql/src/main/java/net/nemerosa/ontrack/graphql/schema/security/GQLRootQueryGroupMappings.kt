package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.security.GroupMappingService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGroupMappings(
    private val gqlTypeGroupMapping: GQLTypeGroupMapping,
    private val groupMappingService: GroupMappingService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("groupMappings")
            .description("List of mappings between IdP groups and Yontrack groups")
            .type(listType(gqlTypeGroupMapping.typeRef))
            .dataFetcher {
                groupMappingService.groupMappings
            }
            .build()
}