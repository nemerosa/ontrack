package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.graphql.schema.authorizations.GQLInterfaceAuthorizableService
import net.nemerosa.ontrack.graphql.support.booleanArgument
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component

@Component
class GQLTypeValidationRun(
    creation: GQLTypeCreation,
    private val validationRunStatus: GQLTypeValidationRunStatus,
    projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
    private val runInfo: GQLTypeRunInfo,
    private val runInfoService: RunInfoService,
    private val validationRunData: GQLTypeValidationRunData,
    private val projectEntityInterface: GQLProjectEntityInterface,
    private val gqlInterfaceAuthorizableService: GQLInterfaceAuthorizableService,
    freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
    extensionManager: ExtensionManager,
) : AbstractGQLProjectEntity<ValidationRun>(
    ValidationRun::class.java,
    ProjectEntityType.VALIDATION_RUN,
    projectEntityFieldContributors,
    creation,
    freeTextAnnotatorContributors,
    extensionManager,
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
                    .type(listType(validationRunStatus.typeRef))
                    .argument(
                        booleanArgument(
                            "lastOnly",
                            "True to get only the last status only."
                        )
                    )
                    .dataFetcher { environment ->
                        val validationRun = environment.getSource<ValidationRun>()
                        val lastOnly = environment.getArgument<Boolean>("lastOnly") ?: false
                        val statuses = if (lastOnly) {
                            validationRun.validationRunStatuses.take(1)
                        } else {
                            validationRun.validationRunStatuses
                        }
                        statuses.map { status ->
                            GQLTypeValidationRunStatus.Data(
                                validationRun,
                                status
                            )
                        }
                    }
            }
            // Last status
            .field {
                    it.name("lastStatus")
                        .description("Last validation status for this run")
                        .type(validationRunStatus.typeRef.toNotNull())
                        .dataFetcher { env ->
                            val validationRun: ValidationRun = env.getSource()
                            GQLTypeValidationRunStatus.Data(
                                validationRun,
                                validationRun.lastStatus
                            )
                        }
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
            // Authorizations
            .apply {
                gqlInterfaceAuthorizableService.apply(this, ValidationRun::class)
            }
            // OK
            .build()

    override fun getSignature(entity: ValidationRun): Signature? {
        return entity.lastStatus.signature
    }

    companion object {
        const val VALIDATION_RUN = "ValidationRun"
    }
}
