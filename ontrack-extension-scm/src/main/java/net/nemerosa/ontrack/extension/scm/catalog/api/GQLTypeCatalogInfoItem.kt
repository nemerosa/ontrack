package net.nemerosa.ontrack.extension.scm.catalog.api

import com.fasterxml.jackson.databind.JsonNode
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeExtensionFeatureDescription
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.GQLScalarLocalDateTime
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GQLTypeCatalogInfoItem(
        private val extensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    class Data(
            val id: String,
            val name: String,
            val data: JsonNode?,
            val error: String?,
            val timestamp: LocalDateTime,
            val feature: ExtensionFeatureDescription
    )

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Catalog info collected")
                    .field {
                        it.name("id")
                                .description("ID of the catalog info contributor")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("name")
                                .description("Display name of the catalog info contributor")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("data")
                                .description("Data collected by the catalog info contributor")
                                .type(GQLScalarJSON.INSTANCE)
                    }
                    .field {
                        it.name("error")
                                .description("Error field")
                                .type(GraphQLString)
                    }
                    .field {
                        it.name("timestamp")
                                .description("Timestamp of the collection")
                                .type(GQLScalarLocalDateTime.INSTANCE)
                    }
                    .field {
                        it.name("feature")
                                .description("Extension feature")
                                .type(extensionFeatureDescription.typeRef)
                    }
                    .build()

    override fun getTypeName(): String = "CatalogInfoItem"

}