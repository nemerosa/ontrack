package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Defines a generic build filter using a `type` and some arbitrary JSON data.
 */
@Component
class GQLInputBuildGenericFilter
@Autowired
constructor(private val buildFilterService: BuildFilterService) : GQLInputType<BuildFilterProviderData<*>> {

    override fun getInputType(): GraphQLInputType {
        return GraphQLInputObjectType.newInputObject()
                .name("GenericBuildFilter")
                .field {
                    it.name("type")
                            .description("FQCN of the filter type")
                            .type(GraphQLNonNull(Scalars.GraphQLString))
                }
                .field {
                    it.name("data")
                            .description("Filter data as JSON")
                            .type(Scalars.GraphQLString)
                }
                .build()
    }

    override fun convert(argument: Any?): BuildFilterProviderData<*> {
        if (argument == null) {
            return buildFilterService.standardFilterProviderData(10).build()
        } else if (argument is Map<*, *>) {
            val type = argument["type"] as String
            val data = argument["data"] as String?
            // Parses the data
            val dataNode = if (data != null) JsonUtils.parseAsNode(data) else null
            // Gets the build filter
            return buildFilterService.getBuildFilterProviderData<Any>(type, dataNode)
        } else {
            throw IllegalStateException("Unexpected generic filter format")
        }
    }

}
