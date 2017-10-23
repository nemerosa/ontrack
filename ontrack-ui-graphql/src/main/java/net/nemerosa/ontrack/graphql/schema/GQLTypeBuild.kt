package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class GQLTypeBuild
@Autowired
constructor(
        private val structureService: StructureService,
        private val projectEntityInterface: GQLProjectEntityInterface,
        private val validation: GQLTypeValidation,
        creation: GQLTypeCreation,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>
) : AbstractGQLProjectEntity<Build>(Build::class.java, ProjectEntityType.BUILD, projectEntityFieldContributors, creation) {

    override fun getTypeRef() = GraphQLTypeReference(BUILD)

    override fun createType(): GraphQLObjectType {
        return newObject()
                .name(BUILD)
                .withInterface(projectEntityInterface.typeRef)
                .fields(projectEntityInterfaceFields())
                // Ref to branch
                .field(
                        newFieldDefinition()
                                .name("branch")
                                .description("Reference to branch")
                                .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
                                .build()
                )
                // Promotion runs
                .field(
                        newFieldDefinition()
                                .name("promotionRuns")
                                .description("Promotions for this build")
                                .argument(
                                        newArgument()
                                                .name("promotion")
                                                .description("Name of the promotion level")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name("lastPerLevel")
                                                .description("Returns the last promotion run per promotion level")
                                                .type(GraphQLBoolean)
                                                .build()
                                )
                                .type(stdList(GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN)))
                                .dataFetcher(buildPromotionRunsFetcher())
                                .build()
                )
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .description("Validations for this build")
                                .argument(
                                        newArgument()
                                                .name("validationStamp")
                                                .description("Name of the validation stamp")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name("count")
                                                .description("Maximum number of validation runs")
                                                .type(GraphQLInt)
                                                .defaultValue(50)
                                                .build()
                                )
                                .type(stdList(GraphQLTypeReference(GQLTypeValidationRun.VALIDATION_RUN)))
                                .dataFetcher(buildValidationRunsFetcher())
                                .build()
                )
                // Validation runs per validation stamp
                .field { f ->
                    f.name("validations")
                            .description("Validations per validation stamp")
                            .argument(
                                    newArgument()
                                            .name("validationStamp")
                                            .description("Name of the validation stamp")
                                            .type(GraphQLString)
                                            .build()
                            )
                            .type(stdList(validation.typeRef))
                            .dataFetcher(buildValidationsFetcher())
                }
                // Build links
                .field(
                        newFieldDefinition()
                                .name("linkedBuilds")
                                .description("Builds this build is linked to")
                                .type(stdList(GraphQLTypeReference(BUILD)))
                                .dataFetcher(buildLinkedToFetcher())
                                .build()
                )
                // OK
                .build()
    }

    private fun buildValidationsFetcher(): DataFetcher<List<GQLTypeValidation.GQLTypeValidationData>> {
        return fetcher(
                Build::class.java
        ) { environment: DataFetchingEnvironment, build: Build ->
            // Filter on validation stamp
            val validationStampName = GraphqlUtils.getStringArgument(environment, "validationStamp")
            if (validationStampName.isPresent) {
                // Loads the validation stamp by name
                return@fetcher structureService.findValidationStampByName(
                        build.project.name,
                        build.branch.name,
                        validationStampName.get()
                ).map { vs ->
                    listOf(
                            buildValidation(
                                    vs, build
                            )
                    )
                }.orElseThrow {
                    ValidationStampNotFoundException(
                            build.project.name,
                            build.branch.name,
                            validationStampName.get()
                    )
                }
            } else {
                // Gets the validation stamps for the branch
                return@fetcher structureService.getValidationStampListForBranch(build.branch.id)
                        .map { validationStamp -> buildValidation(validationStamp, build) }
            }
        }
    }

    private fun buildValidation(
            validationStamp: ValidationStamp,
            build: Build
    ): GQLTypeValidation.GQLTypeValidationData {
        return GQLTypeValidation.GQLTypeValidationData(
                validationStamp,
                structureService.getValidationRunsForBuildAndValidationStamp(
                        build.id,
                        validationStamp.id
                )
        )
    }

    private fun buildLinkedToFetcher(): DataFetcher<List<Build>> {
        return fetcher(
                Build::class.java,
                structureService::getBuildLinksFrom
        )
    }

    private fun buildValidationRunsFetcher() =
            DataFetcher<List<ValidationRun>> { environment ->
                val build: Build = environment.getSource()
                // Filter
                val count = GraphqlUtils.getIntArgument(environment, "count").orElse(50)
                val validation = GraphqlUtils.getStringArgument(environment, "validation").orElse(null)
                if (validation != null) {
                    // Gets the validation stamp
                    val validationStamp = structureService.findValidationStampByName(
                            build.project.name,
                            build.branch.name,
                            validation
                    ).orElseThrow {
                        ValidationStampNotFoundException(
                                build.project.name,
                                build.branch.name,
                                validation
                        )
                    }
                    // Gets validations runs for this validation level
                    return@DataFetcher structureService.getValidationRunsForBuildAndValidationStamp(
                            build.id,
                            validationStamp.id
                    ).take(count)
                } else {
                    // Gets all the validation runs (limited by count)
                    return@DataFetcher structureService.getValidationRunsForBuild(build.id)
                            .take(count)
                }
            }

    private fun buildPromotionRunsFetcher() =
            DataFetcher<List<PromotionRun>> { environment ->
                val build: Build = environment.getSource()
                // Last per promotion filter?
                val lastPerLevel = GraphqlUtils.getBooleanArgument(environment, "lastPerLevel", false)
                // Promotion filter
                val promotion = GraphqlUtils.getStringArgument(environment, "promotion").orElse(null)
                if (promotion != null) {
                    // Gets the promotion level
                    val promotionLevel = structureService.findPromotionLevelByName(
                            build.project.name,
                            build.branch.name,
                            promotion
                    ).orElseThrow {
                        PromotionLevelNotFoundException(
                                build.project.name,
                                build.branch.name,
                                promotion
                        )
                    }
                    // Gets promotion runs for this promotion level
                    if (lastPerLevel) {
                        return@DataFetcher structureService.getLastPromotionRunForBuildAndPromotionLevel(build, promotionLevel)
                                .map { listOf(it) }
                                .orElse(listOf())
                    } else {
                        return@DataFetcher structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
                    }
                } else {
                    // Gets all the promotion runs
                    if (lastPerLevel) {
                        return@DataFetcher structureService.getLastPromotionRunsForBuild(build.id)
                    } else {
                        return@DataFetcher structureService.getPromotionRunsForBuild(build.id)
                    }
                }
            }

    override fun getSignature(entity: Build): Optional<Signature> {
        return Optional.ofNullable(entity.signature)
    }

    companion object {
        @JvmField
        val BUILD = "Build"
    }
}
