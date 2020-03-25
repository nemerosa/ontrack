package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.*
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.fetcher
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.exceptions.ValidationStampNotFoundException
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component

@Component
class GQLTypeBuild(
        private val structureService: StructureService,
        private val projectEntityInterface: GQLProjectEntityInterface,
        private val validation: GQLTypeValidation,
        private val validationRun: GQLTypeValidationRun,
        private val runInfo: GQLTypeRunInfo,
        private val runInfoService: RunInfoService,
        private val paginatedListFactory: GQLPaginatedListFactory,
        creation: GQLTypeCreation,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
) : AbstractGQLProjectEntity<Build>(Build::class.java, ProjectEntityType.BUILD, projectEntityFieldContributors, creation, freeTextAnnotatorContributors) {

    override fun getTypeName() = BUILD

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
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
                                                .name(ARG_PROMOTION)
                                                .description("Name of the promotion level")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name(ARG_LAST_PER_LEVEL)
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
                                                .name(ARG_VALIDATION_STAMP)
                                                .description("Name of the validation stamp")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument(
                                        newArgument()
                                                .name(ARG_COUNT)
                                                .description("Maximum number of validation runs")
                                                .type(GraphQLInt)
                                                .defaultValue(50)
                                                .build()
                                )
                                .type(stdList(GraphQLTypeReference(GQLTypeValidationRun.VALIDATION_RUN)))
                                .dataFetcher(buildValidationRunsFetcher())
                                .build()
                )

                // Paginated list of validation runs
                .field(
                        paginatedListFactory.createPaginatedField<Build, ValidationRun>(
                                cache = cache,
                                fieldName = "validationRunsPaginated",
                                fieldDescription = "Paginated list of validation runs",
                                itemType = validationRun,
                                itemListCounter = { _, build ->
                                    structureService.getValidationRunsCountForBuild(
                                            build.id
                                    )
                                },
                                itemListProvider = { _, build, offset, size ->
                                    structureService.getValidationRunsForBuild(
                                            build.id,
                                            offset,
                                            size
                                    )
                                }
                        )
                )
                // Validation runs per validation stamp
                .field { f ->
                    f.name("validations")
                            .description("Validations per validation stamp")
                            .argument(
                                    newArgument()
                                            .name(ARG_VALIDATION_STAMP)
                                            .description("Name of the validation stamp")
                                            .type(GraphQLString)
                                            .build()
                            )
                            .type(stdList(validation.typeRef))
                            .dataFetcher(buildValidationsFetcher())
                }
                // Build links
                .field { f ->
                    f.name("linkedBuilds")
                            .deprecate("Use `using` and `usedBy` fields instead.")
                            .description("Builds this build is linked to")
                            .argument { a ->
                                a.name(ARG_DIRECTION)
                                        .description("Direction of the link to follow.")
                                        .type(
                                                GraphQLEnumType.newEnum()
                                                        .name("BuildLinkDirection")
                                                        .description("Direction for build links.")
                                                        .value("TO")
                                                        .value("FROM")
                                                        .value("BOTH")
                                                        .build()
                                        )
                                        .defaultValue("TO")
                            }
                            .type(stdList(GraphQLTypeReference(BUILD)))
                            .dataFetcher(buildLinkedFetcher())
                }
                // Build links - "uses" direction, no pagination needed
                .field { f ->
                    f.name("uses")
                            .deprecate("Use `using` field instead.")
                            .description("List of builds being used by this one.")
                            .type(stdList(GraphQLTypeReference(BUILD)))
                            .argument {
                                it.name("project")
                                        .description("Keeps only links targeted to this project")
                                        .type(GraphQLString)
                            }
                            .argument {
                                it.name("branch")
                                        .description("Keeps only links targeted to this branch. `project` argument is also required.")
                                        .type(GraphQLString)
                            }
                            .dataFetcher(buildBeingUsedFetcher())
                }
                // Build links - "using" direction, with pagination
                .field(
                        paginatedListFactory.createPaginatedField<Build, Build>(
                                cache = cache,
                                fieldName = "using",
                                fieldDescription = "List of builds being used by this one.",
                                itemType = this,
                                arguments = listOf(
                                        newArgument()
                                                .name("project")
                                                .description("Keeps only links targeted from this project")
                                                .type(GraphQLString)
                                                .build(),
                                        newArgument()
                                                .name("branch")
                                                .description("Keeps only links targeted from this branch. `project` argument is also required.")
                                                .type(GraphQLString)
                                                .build()
                                ),
                                itemPaginatedListProvider = { environment, build, offset, size ->
                                    val filter: (Build) -> Boolean = getFilter(environment)
                                    structureService.getBuildsUsedBy(
                                            build,
                                            offset,
                                            size,
                                            filter
                                    )
                                }
                        )
                )
                // Build links - "usedBy" direction, with pagination
                .field(
                        paginatedListFactory.createPaginatedField<Build, Build>(
                                cache = cache,
                                fieldName = "usedBy",
                                fieldDescription = "List of builds using this one.",
                                itemType = this,
                                arguments = listOf(
                                        newArgument()
                                                .name("project")
                                                .description("Keeps only links targeted from this project")
                                                .type(GraphQLString)
                                                .build(),
                                        newArgument()
                                                .name("branch")
                                                .description("Keeps only links targeted from this branch. `project` argument is also required.")
                                                .type(GraphQLString)
                                                .build()
                                ),
                                itemPaginatedListProvider = { environment, build, offset, size ->
                                    val filter = getFilter(environment)
                                    structureService.getBuildsUsing(
                                            build,
                                            offset,
                                            size,
                                            filter
                                    )
                                }
                        )
                )
                // Link direction (only used for linked builds)
                .field { f ->
                    f.name(ARG_DIRECTION)
                            .description("Link direction")
                            .deprecate("Use `uses` and `usedBy` fields on the `Build` type instead.")
                            .type(GraphQLString)
                            .dataFetcher(fetcher(LinkedBuild::class.java, LinkedBuild::direction))
                }
                // Run info
                .field {
                    it.name("runInfo")
                            .description("Run info associated with this build")
                            .type(runInfo.typeRef)
                            .runInfoFetcher<Build> { entity -> runInfoService.getRunInfo(entity) }
                }
                // OK
                .build()
    }

    private fun getFilter(environment: DataFetchingEnvironment): (Build) -> Boolean {
        val projectName: String? = environment.getArgument("project")
        val branchName: String? = environment.getArgument("branch")
        val filter: (Build) -> Boolean
        if (branchName != null) {
            if (projectName == null) {
                throw IllegalArgumentException("`project` is required")
            } else {
                filter = {
                    it.branch.project.name == projectName && it.branch.name == branchName
                }
            }
        } else if (projectName != null) {
            filter = {
                it.branch.project.name == projectName
            }
        } else {
            filter = { true }
        }
        return filter
    }

    private fun buildValidationsFetcher(): DataFetcher<List<GQLTypeValidation.GQLTypeValidationData>> {
        return fetcher(
                Build::class.java
        ) { environment: DataFetchingEnvironment, build: Build ->
            // Filter on validation stamp
            val validationStampName = GraphqlUtils.getStringArgument(environment, ARG_VALIDATION_STAMP)
            if (validationStampName.isPresent) {
                val validationStamp: ValidationStamp? =
                        structureService.findValidationStampByName(
                                build.project.name,
                                build.branch.name,
                                validationStampName.get()
                        ).orElse(null)
                if (validationStamp != null) {
                    return@fetcher listOf(
                            buildValidation(
                                    validationStamp, build
                            )
                    )
                } else {
                    return@fetcher listOf<GQLTypeValidation.GQLTypeValidationData>()
                }
            } else {
                // Gets the validation runs for the build
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

    private fun buildBeingUsedFetcher(): DataFetcher<List<Build>> {
        return DataFetcher { environment ->
            val build: Build = environment.getSource()
            val links = structureService.getBuildLinksFrom(build)
            val projectName: String? = environment.getArgument("project")
            val branchName: String? = environment.getArgument("branch")
            if (branchName != null) {
                if (projectName == null) {
                    throw IllegalArgumentException("`project` is required")
                } else {
                    links.filter { link ->
                        link.branch.project.name == projectName && link.branch.name == branchName
                    }
                }
            } else if (projectName != null) {
                links.filter { link ->
                    link.branch.project.name == projectName
                }
            } else {
                links
            }
        }
    }

    private fun buildLinkedFetcher(): DataFetcher<List<Build>> {
        return DataFetcher { env ->
            val build = env.getSource<Build>()
            when (env.getArgument(ARG_DIRECTION) ?: "TO") {
                "TO" -> getLinkedBuilds(structureService.getBuildLinksFrom(build), "to")
                "FROM" -> getLinkedBuilds(structureService.getBuildLinksTo(build), "from")
                "BOTH" -> getLinkedBuilds(structureService.getBuildLinksFrom(build), "to") +
                        getLinkedBuilds(structureService.getBuildLinksTo(build), "from")
                else -> getLinkedBuilds(structureService.getBuildLinksFrom(build), "to")
            }
        }
    }

    private fun getLinkedBuilds(builds: List<Build>, direction: String) =
            builds.map { LinkedBuild(it, direction) }

    private fun buildValidationRunsFetcher() =
            DataFetcher<List<ValidationRun>> { environment ->
                val build: Build = environment.getSource()
                // Filter
                val count = GraphqlUtils.getIntArgument(environment, ARG_COUNT).orElse(50)
                val validation = GraphqlUtils.getStringArgument(environment, ARG_VALIDATION_STAMP).orElse(null)
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
                val lastPerLevel = GraphqlUtils.getBooleanArgument(environment, ARG_LAST_PER_LEVEL, false)
                // Promotion filter
                val promotion = GraphqlUtils.getStringArgument(environment, ARG_PROMOTION).orElse(null)
                val promotionLevel: PromotionLevel? = if (promotion != null) {
                    // Gets the promotion level
                    structureService.findPromotionLevelByName(
                            build.project.name,
                            build.branch.name,
                            promotion
                    ).orElse(null)
                } else {
                    null
                }
                if (promotionLevel != null) {
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

    override fun getSignature(entity: Build): Signature? {
        return entity.signature
    }

    companion object {
        /**
         * Name of the type
         */
        const val BUILD = "Build"
        /**
         * Filter on the validation runs
         */
        const val ARG_VALIDATION_STAMP = "validationStamp"
        /**
         * Count argument
         */
        const val ARG_COUNT = "count"
        /**
         * Promotion level argument
         */
        const val ARG_PROMOTION = "promotion"
        /**
         * Last per level argument
         */
        const val ARG_LAST_PER_LEVEL = "lastPerLevel"
        /**
         * Direction argument
         */
        const val ARG_DIRECTION = "direction"
    }
}

class LinkedBuild(
        build: Build,
        val direction: String
) : Build(
        build.id,
        build.name,
        build.description,
        build.signature,
        build.branch
)
