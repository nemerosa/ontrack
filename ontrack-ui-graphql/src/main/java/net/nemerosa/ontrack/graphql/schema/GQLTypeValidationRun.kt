package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component
import java.util.*

@Component
class GQLTypeValidationRun(
        creation: GQLTypeCreation,
        private val validationRunStatus: GQLTypeValidationRunStatus,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val runInfo: GQLTypeRunInfo,
        private val runInfoService: RunInfoService,
        private val validationRunData: GQLTypeValidationRunData,
        private val projectEntityInterface: GQLProjectEntityInterface
) : AbstractGQLProjectEntity<ValidationRun>(
        ValidationRun::class.java,
        ProjectEntityType.VALIDATION_RUN,
        projectEntityFieldContributors,
        creation
) {

    override fun getTypeName() = VALIDATION_RUN

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            newObject()
                    .name(VALIDATION_RUN)
                    .withInterface(projectEntityInterface.typeRef)
                    .fields(projectEntityInterfaceFields())
                    // Build
                    .field {
                        it
                                .name("build")
                                .description("Associated build")
                                .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeBuild.BUILD)))
                    }
                    // Promotion level
                    .field {
                        it
                                .name("validationStamp")
                                .description("Associated validation stamp")
                                .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeValidationStamp.VALIDATION_STAMP)))
                    }
                    // Run order
                    .field {
                        it
                                .name("runOrder")
                                .description("Run order")
                                .type(GraphQLInt)
                    }
                    // Validation statuses
                    .field {
                        it
                                .name("validationRunStatuses")
                                .description("List of validation statuses")
                                .type(stdList(validationRunStatus.typeRef))
                    }
                    // Run info
                    .field {
                        it.name("runInfo")
                                .description("Run info associated with this validation run")
                                .type(runInfo.typeRef)
                                .runInfoFetcher<ValidationRun> { runInfoService.getRunInfo(it) }
                    }
                    // Data
                    .field {
                        it.name("data")
                                .description("Data associated with the validation run")
                                .type(validationRunData.typeRef)
                    }
                    // OK
                    .build()

    override fun getSignature(entity: ValidationRun): Optional<Signature> {
        return Optional.ofNullable(entity.lastStatus.signature)
    }

    companion object {
        const val VALIDATION_RUN = "ValidationRun"
    }
}
