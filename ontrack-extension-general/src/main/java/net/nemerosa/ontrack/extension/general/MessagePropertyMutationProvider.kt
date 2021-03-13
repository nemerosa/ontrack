package net.nemerosa.ontrack.extension.general

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.MutationInput
import net.nemerosa.ontrack.graphql.schema.PropertyMutationProvider
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class MessagePropertyMutationProvider : PropertyMutationProvider<MessageProperty> {

    override val propertyType: KClass<out PropertyType<MessageProperty>> = MessagePropertyType::class
    override val mutationNameFragment: String = "Message"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        GraphQLInputObjectField.newInputObjectField()
            .name("type")
            .description("Type of message")
            .type(GraphQLNonNull(GraphQLString))
            .build(),
        GraphQLInputObjectField.newInputObjectField()
            .name("text")
            .description("Text of the message")
            .type(GraphQLNonNull(GraphQLString))
            .build()
    )

    override fun readInput(input: MutationInput) = MessageProperty(
        MessageType.valueOf(input.getRequiredInput("type")),
        input.getRequiredInput("text")
    )

}