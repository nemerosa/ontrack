package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.RunInfoInput
import org.springframework.stereotype.Component

@Component
class GQLInputRunInfoInput : GQLInputType<RunInfoInput> {

    override fun createInputType(): GraphQLInputType = GraphQLInputObjectType.newInputObject()
        .name(RunInfoInput::class.java.simpleName)
        .description("Input for some run info")
        .fields(GraphQLBeanConverter.asInputFields(RunInfoInput::class))
        .build()

    override fun convert(argument: Any?): RunInfoInput =
        argument.asJson().parse()

    override fun getTypeRef() = GraphQLTypeReference(RunInfoInput::class.java.simpleName)

}