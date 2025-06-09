package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.schema.authorizations.GQLInterfaceAuthorizableService
import net.nemerosa.ontrack.graphql.support.descriptionField
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.stereotype.Component

@Component
class GQLTypeValidationRunStatus(
    private val fieldContributors: List<GQLFieldContributor>,
    private val validationRunStatusID: GQLTypeValidationRunStatusID,
    private val creation: GQLTypeCreation,
    private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
    private val gqlInterfaceAuthorizableService: GQLInterfaceAuthorizableService,
) : GQLType {

    override fun getTypeName() = VALIDATION_RUN_STATUS

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
            .name(VALIDATION_RUN_STATUS)
            // ID
            .field {
                it.name("id")
                    .type(GraphQLNonNull(GraphQLInt))
                    .dataFetcher { environment ->
                        val data = environment.getSource<Data>()!!
                        data.delegate.id()
                    }
            }
            // Creation
            .field {
                it.name("creation")
                    .type(creation.typeRef)
                    .dataFetcher(GQLTypeCreation.dataFetcher<Data> { gqlTypeValidationRunStatusWrapper ->
                        gqlTypeValidationRunStatusWrapper.delegate.signature
                    })
            }
            // Status ID
            .field(
                newFieldDefinition()
                    .name("statusID")
                    .description("Status ID")
                    .type(validationRunStatusID.typeRef)
                    .build()
            )
            // Description
            .field(descriptionField())
            // Annotated description
            .field {
                it.name("annotatedDescription")
                    .type(Scalars.GraphQLString)
                    .description("Description with links.")
                    .dataFetcher { env ->
                        val data: Data = env.getSource()!!
                        annotatedDescription(data)
                    }
            }
            // Links
            .fields(ValidationRunStatus::class.java.graphQLFieldContributions(fieldContributors))
            // Authorizations
            .apply {
                gqlInterfaceAuthorizableService.apply(this, ValidationRunStatus::class) { data: Data ->
                    data.delegate
                }
            }
            // OK
            .build()

    }

    private fun annotatedDescription(wrapper: Data): String {
        val description: String? = wrapper.description
        return if (description.isNullOrBlank()) {
            ""
        } else {
            val validationRun = wrapper.validationRun
            // Gets the list of message annotators to use
            val annotators = freeTextAnnotatorContributors.flatMap { it.getMessageAnnotators(validationRun) }
            // Annotates the message
            MessageAnnotationUtils.annotate(description, annotators)
        }
    }

    companion object {
        const val VALIDATION_RUN_STATUS = "ValidationRunStatus"
    }

    class Data(
        val validationRun: ValidationRun,
        val delegate: ValidationRunStatus
    ) {
        val statusID: ValidationRunStatusID get() = delegate.statusID
        val description: String? get() = delegate.description
    }

}
