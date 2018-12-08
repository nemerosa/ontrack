package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GQLTypeValidationRunStatus
@Autowired
constructor(
        private val validationRunStatusID: GQLTypeValidationRunStatusID,
        private val creation: GQLTypeCreation,
        private val freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
) : GQLType {

    override fun getTypeName() = VALIDATION_RUN_STATUS

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
                .name(VALIDATION_RUN_STATUS)
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
                .field(GraphqlUtils.descriptionField())
                // Annotated description
                .field {
                    it.name("annotatedDescription")
                            .type(Scalars.GraphQLString)
                            .description("Description with links.")
                            .dataFetcher(
                                    GraphqlUtils.fetcher(Data::class.java) { wrapper ->
                                        annotatedDescription(wrapper)
                                    }
                            )
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
            val annotators = freeTextAnnotatorContributors.mapNotNull { it.getMessageAnnotator(validationRun) }
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
