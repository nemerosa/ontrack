package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component
import java.util.*

@Component
class GQLTypeValidationRun(
        creation: GQLTypeCreation,
        private val validationRunStatus: GQLTypeValidationRunStatus,
        private val validationRunData: GQLTypeValidationRunData,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val projectEntityInterface: GQLProjectEntityInterface
) : AbstractGQLProjectEntity<ValidationRun>(
        ValidationRun::class.java,
        ProjectEntityType.VALIDATION_RUN,
        projectEntityFieldContributors,
        creation
) {

    override fun getTypeName() = VALIDATION_RUN

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
                .name(VALIDATION_RUN)
                .withInterface(projectEntityInterface.typeRef)
                .fields(projectEntityInterfaceFields())
                // Build
                .field(
                        newFieldDefinition()
                                .name("build")
                                .description("Associated build")
                                .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeBuild.BUILD)))
                                .build()
                )
                // Promotion level
                .field(
                        newFieldDefinition()
                                .name("validationStamp")
                                .description("Associated validation stamp")
                                .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeValidationStamp.VALIDATION_STAMP)))
                                .build()
                )
                // Run order
                .field(
                        newFieldDefinition()
                                .name("runOrder")
                                .description("Run order")
                                .type(GraphQLInt)
                                .build()
                )
                // Validation statuses
                .field(
                        newFieldDefinition()
                                .name("validationRunStatuses")
                                .description("List of validation statuses")
                                .type(stdList(validationRunStatus.typeRef))
                                .build()
                )
                // Data
                .field {
                    it.name("data")
                            .description("Data associated with the validation run")
                            .type(validationRunData.typeRef)
                }
                // OK
                .build()

    }

    override fun getSignature(entity: ValidationRun) =
            Optional.ofNullable(entity.lastStatus.signature)

    companion object {
        @JvmField
        val VALIDATION_RUN = "ValidationRun"
    }
}
