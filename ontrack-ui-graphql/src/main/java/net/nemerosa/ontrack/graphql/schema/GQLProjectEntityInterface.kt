package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLTypeReference
import graphql.schema.TypeResolverProxy
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class GQLProjectEntityInterface @Autowired
constructor(private val creation: GQLTypeCreation) : GQLInterface {

    override fun getTypeRef() = GraphQLTypeReference(PROJECT_ENTITY)

    override fun createInterface(): GraphQLInterfaceType {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                // Common fields
                .fields(baseProjectEntityInterfaceFields())
                // TODO Type resolver not set, but it should
                .typeResolver(TypeResolverProxy())
                // OK
                .build()
    }

    private fun baseProjectEntityInterfaceFields(): List<GraphQLFieldDefinition> {
        return ArrayList(
                Arrays.asList(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField(),
                        newFieldDefinition()
                                .name("creation")
                                .type(creation.typeRef)
                                .build()
                )
        )
    }

    companion object {
        @JvmField
        val PROJECT_ENTITY = "ProjectEntity"
    }

}
