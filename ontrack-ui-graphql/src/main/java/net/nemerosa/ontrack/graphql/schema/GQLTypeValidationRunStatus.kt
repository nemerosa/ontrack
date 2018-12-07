package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
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
                            .dataFetcher(GQLTypeCreation.dataFetcher<ValidationRunStatus> { it.signature })
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
                            .dataFetcher {
                                GraphqlUtils.fetcher(ValidationRunStatus::class.java) { status ->
                                    annotatedDescription(status)
                                }
                            }
                }
                // OK
                .build()

    }

    private fun annotatedDescription(validationRunStatus: ValidationRunStatus): String {
        val description: String? = validationRunStatus.description
        if (description.isNullOrBlank()) {
            return ""
        } else {
            // Gets the list of message annotators to use
            val annotators = freeTextAnnotatorContributors.map { it.messageAnnotator }
            // Annotates the message
            return MessageAnnotationUtils.annotate(description, annotators)
        }
    }

    companion object {

        val VALIDATION_RUN_STATUS = "ValidationRunStatus"
    }

}
