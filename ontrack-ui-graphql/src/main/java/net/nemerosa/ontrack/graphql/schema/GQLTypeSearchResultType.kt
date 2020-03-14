package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.structure.SearchResultType
import org.springframework.stereotype.Component

/**
 * Type for [SearchResultType].
 */
@Component
class GQLTypeSearchResultType(
        private val extensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(typeName)
                .description("Type of search result")
                .field {
                    it.name("feature")
                            .description("Associated feature")
                            .type(extensionFeatureDescription.typeRef)
                }
                .field {
                    it.name("id")
                            .description("ID for the type of search result")
                            .type(GraphQLString)
                }
                .field {
                    it.name("name")
                            .description("Display name for the search result")
                            .type(GraphQLString)
                }
                .field {
                    it.name("description")
                            .description("Short help text explaining the format of the token")
                            .type(GraphQLString)
                }
                .build()
    }

    override fun getTypeName(): String = SearchResultType::class.java.simpleName
}
