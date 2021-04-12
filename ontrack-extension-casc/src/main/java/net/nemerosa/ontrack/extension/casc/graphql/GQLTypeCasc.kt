package net.nemerosa.ontrack.extension.casc.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.casc.schema.CascSchemaService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class GQLTypeCasc(
    private val cascSchemaService: CascSchemaService,
) : GQLType {

    companion object {
        val instance: Any = "GQLTypeCasc"
    }

    override fun getTypeName(): String = "CasC"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Configuration as code information")
            .field {
                it.name("schema")
                    .description("CasC schema as JSON")
                    .type(GQLScalarJSON.INSTANCE)
                    .dataFetcher { _ ->
                        cascSchemaService.schema.asJson()
                    }
            }
            .field {
                it.name("locations")
                    .description("List of resources needed to define the configuration as code")
                    .type(listType(GraphQLString))
                    .dataFetcher { _ ->
                        cascSchemaService.locations
                    }
            }
            .build()
}