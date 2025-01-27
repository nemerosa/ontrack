package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.structure.BuildSearchFormExtension
import org.springframework.stereotype.Component

@Component
class GQLInputBuildSearchFormExtension : GQLInputType<BuildSearchFormExtension> {

    private val typeName: String = BuildSearchFormExtension::class.java.simpleName

    override fun getTypeRef(): GraphQLTypeReference = GraphQLTypeReference(typeName)

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLInputObjectType.newInputObject()
            .name(typeName)
            .description(getAPITypeName(BuildSearchFormExtension::class))
            .field(stringInputField(BuildSearchFormExtension::extension))
            .field(stringInputField(BuildSearchFormExtension::value))
            .build()

    override fun convert(argument: Any?): BuildSearchFormExtension? = argument?.asJson()?.parseOrNull()

}