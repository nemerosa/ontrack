package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.descriptionField
import net.nemerosa.ontrack.graphql.support.idField
import net.nemerosa.ontrack.graphql.support.nameField
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GQLProjectEntityInterface @Autowired
constructor(private val creation: GQLTypeCreation) : GQLInterface {

    override fun getTypeRef() = GraphQLTypeReference(PROJECT_ENTITY)

    override fun createInterface(): GraphQLInterfaceType {
        return GraphQLInterfaceType.newInterface()
            .name(PROJECT_ENTITY)
            // Common fields
            .fields(baseProjectEntityInterfaceFields())
            // Resolving the interface
            .typeResolver { env ->
                val entity = env.getObject<ProjectEntity>()
                val entityTypeName = entity::class.java.simpleName
                env.schema.getObjectType(entityTypeName)
            }
            // OK
            .build()
    }

    private fun baseProjectEntityInterfaceFields(): List<GraphQLFieldDefinition> = listOf(
        idField(),
        nameField(),
        descriptionField(),
        GraphQLFieldDefinition.newFieldDefinition()
            .name("creation")
            .type(creation.typeRef)
            .build()
    )

    companion object {
        const val PROJECT_ENTITY = "ProjectEntity"
    }

}
