package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.ProjectEntityUserMenuItemExtension
import net.nemerosa.ontrack.graphql.support.descriptionField
import net.nemerosa.ontrack.graphql.support.idField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.nameField
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils

abstract class AbstractGQLProjectEntity<T : ProjectEntity>(
    private val projectEntityClass: Class<T>,
    private val projectEntityType: ProjectEntityType,
    private val projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
    private val creation: GQLTypeCreation,
    private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
    private val extensionManager: ExtensionManager,
) : GQLType {

    private val projectEntityUserMenuItemExtensions: Set<ProjectEntityUserMenuItemExtension> by lazy {
        extensionManager.getExtensions(ProjectEntityUserMenuItemExtension::class.java).toSet()
    }

    protected fun projectEntityInterfaceFields(): List<GraphQLFieldDefinition> {
        val definitions = baseProjectEntityInterfaceFields().toMutableList()
        // For all contributors
        definitions.addAll(
            projectEntityFieldContributors
                .mapNotNull { contributor: GQLProjectEntityFieldContributor ->
                    contributor.getFields(
                        projectEntityClass,
                        projectEntityType
                    )
                }
                .flatten()
        )
        // OK
        return definitions
    }

    private fun baseProjectEntityInterfaceFields(): List<GraphQLFieldDefinition> =
        listOf(
            idField(),
            nameField(),
            descriptionField(),
            creationField(),
            annotatedDescriptionField(),
            userMenuActionsField(),
        )

    private fun userMenuActionsField(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("userMenuActions")
            .description("List of actions available for this entity")
            .type(listType(GQLTypeUserMenuAction.TYPE))
            .dataFetcher { env ->
                val entity: T = env.getSource()
                projectEntityUserMenuItemExtensions.flatMap { extension ->
                    extension.getItems(entity)
                }
            }
            .build()

    private fun annotatedDescriptionField(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("annotatedDescription")
            .type(Scalars.GraphQLString)
            .description("Description with links.")
            .dataFetcher { env ->
                val entity: T = env.getSource()
                val description: String? = entity.description
                if (description.isNullOrBlank()) {
                    ""
                } else {
                    val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(entity) }
                    MessageAnnotationUtils.annotate(description, annotators)
                }
            }
            .build()

    private fun creationField(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("creation")
            .type(creation.typeRef)
            .dataFetcher { env ->
                val entity: T = env.getSource()
                val signature = getSignature(entity)
                signature?.let {
                    GQLTypeCreation.getCreationFromSignature(it)
                }
            }
            .build()

    protected abstract fun getSignature(entity: T): Signature?

}