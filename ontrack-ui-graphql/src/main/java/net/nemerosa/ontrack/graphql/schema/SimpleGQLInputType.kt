package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import kotlin.reflect.KClass

abstract class SimpleGQLInputType<T : Any>(
    private val type: KClass<T>
) : GQLInputType<T> {

    override fun createInputType(): GraphQLInputType =
        GraphQLInputObjectType.newInputObject()
            .name(type.java.simpleName)
            .description(getDescription(type))
            .fields(GraphQLBeanConverter.asInputFields(type))
            .build()

    override fun convert(argument: Any?): T? =
        argument?.asJson()?.parseInto(type)

    override fun getTypeRef(): GraphQLTypeReference =
        GraphQLTypeReference(type.java.simpleName)

}