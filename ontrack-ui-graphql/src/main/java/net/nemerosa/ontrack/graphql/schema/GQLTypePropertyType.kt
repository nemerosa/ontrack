package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component

@Component
class GQLTypePropertyType(
    private val gqlProjectEntityType: GQLEnumProjectEntityType
) : GQLType {

    override fun getTypeName(): String = PropertyType::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Definition for a property")
            .stringField(GQLTypePropertyTypeData::typeName, "Full type name for the property")
            .stringField(GQLTypePropertyTypeData::name, "Property display name")
            .stringField(GQLTypePropertyTypeData::description, "Property description")
            .field {
                it.name(GQLTypePropertyTypeData::supportedEntityTypes.name)
                    .description("List of project entity types supported by this property")
                    .type(listType(gqlProjectEntityType.getTypeRef()))
            }
            .build()

    class GQLTypePropertyTypeData(
        val typeName: String,
        val name: String,
        val description: String,
        val supportedEntityTypes: List<ProjectEntityType>
    )

}