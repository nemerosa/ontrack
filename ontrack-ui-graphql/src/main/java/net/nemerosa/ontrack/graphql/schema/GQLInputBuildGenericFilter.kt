package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import org.apache.commons.lang3.StringUtils
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
                    it.name(FIELD_TYPE)
                            .description("FQCN of the filter type, null if no filter is to be applied")
                            .type(Scalars.GraphQLString)
                }
                .field {
                    it.name(FIELD_DATA)
                            .description("Filter data as JSON")
                            .type(Scalars.GraphQLString)
                }
                .build()
    }

    override fun convert(argument: Any?): BuildFilterProviderData<*> {
        if (argument == null) {
            return buildFilterService.standardFilterProviderData(10).build()
        } else if (argument is Map<*, *>) {
            val type = argument[FIELD_TYPE] as String?
            val data = argument[FIELD_DATA] as String?
            // Parses the data
            val dataNode = if (data != null && StringUtils.isNotBlank(data)) JsonUtils.parseAsNode(data) else null
            // If no type is defined, use the default filter
            if (type == null) {
                return buildFilterService.standardFilterProviderData(10).build()
            }
            // Gets the build filter
            else {
                return buildFilterService.getBuildFilterProviderData<Any>(type, dataNode)
            }
        } else {
            throw IllegalStateException("Unexpected generic filter format")
        }
    }

    companion object {
        val FIELD_TYPE = "type"
        val FIELD_DATA = "data"
    }

}
