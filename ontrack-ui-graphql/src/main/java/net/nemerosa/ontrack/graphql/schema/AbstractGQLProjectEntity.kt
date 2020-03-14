package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import java.util.*

abstract class AbstractGQLProjectEntity<T : ProjectEntity>(
        private val projectEntityClass: Class<T>,
        private val projectEntityType: ProjectEntityType,
        private val projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val creation: GQLTypeCreation
) : GQLType {

    protected fun projectEntityInterfaceFields(): List<GraphQLFieldDefinition> {
        val definitions = baseProjectEntityInterfaceFields()
        // For all contributors
        definitions.addAll(
                projectEntityFieldContributors
                        .mapNotNull { contributor: GQLProjectEntityFieldContributor -> contributor.getFields(projectEntityClass, projectEntityType) }
                        .flatten()
        )
        // OK
        return definitions
    }

    private fun baseProjectEntityInterfaceFields(): MutableList<GraphQLFieldDefinition> {
        return ArrayList(
                listOf(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField(),
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("creation")
                                .type(creation.typeRef)
                                .dataFetcher { env ->
                                    val entity: T = env.getSource<T>()
                                    val signature = getSignature(entity)
                                    signature?.let {
                                        GQLTypeCreation.getCreationFromSignature(it)
                                    }
                                }
                                .build()
                )
        )
    }

    protected abstract fun getSignature(entity: T): Signature?

}