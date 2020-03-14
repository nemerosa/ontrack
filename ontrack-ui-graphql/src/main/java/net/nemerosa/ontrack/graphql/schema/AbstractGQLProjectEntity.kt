package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import java.util.*

abstract class AbstractGQLProjectEntity<T : ProjectEntity>(
        private val projectEntityClass: Class<T>,
        private val projectEntityType: ProjectEntityType,
        private val projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val creation: GQLTypeCreation,
        private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
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
                                .build(),
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("annotatedDescription")
                                .type(Scalars.GraphQLString)
                                .description("Description with links.")
                                .dataFetcher { env ->
                                    val entity: T = env.getSource<T>()
                                    val description: String? = entity.description
                                    if (description.isNullOrBlank()) {
                                        ""
                                    } else {
                                        val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(entity) }
                                        MessageAnnotationUtils.annotate(description, annotators)
                                    }
                                }
                                .build()
                )
        )
    }

    protected abstract fun getSignature(entity: T): Signature?

}