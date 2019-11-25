package net.nemerosa.ontrack.extension.issues.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import net.nemerosa.ontrack.extension.issues.IssueServiceExtensionService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.tx.TransactionService
import org.springframework.stereotype.Component

/**
 * Adds a `validationIssues` to the `Branch` GraphQL type, containing
 * the list of issues reported into the validation run statuses.
 */
@Component
class BranchValidationIssuesGraphQLFieldContributor(
        private val validationIssue: GQLTypeValidationIssue,
        private val structureService: StructureService,
        private val transactionService: TransactionService,
        private val validationRunStatusService: ValidationRunStatusService,
        private val issueServiceExtensionService: IssueServiceExtensionService
) : GQLProjectEntityFieldContributor {
    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.BRANCH) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("validationIssues")
                                .description("List of issues reported into the validation run statuses")
                                .type(stdList(validationIssue.typeRef))
                                .argument {
                                    it.name("passed")
                                            .description("Filters the validation runs according to their status")
                                            .type(GraphQLBoolean)
                                }
                                .argument {
                                    it.name("status")
                                            .description("List of issue statuses to keep")
                                            .type(GraphQLList(GraphQLString))
                                }
                                .dataFetcher { env ->
                                    val branch: Branch = env.getSource()
                                    val passed: Boolean? = env.getArgument("passed")
                                    val status: List<String>? = env.getArgument("status")
                                    getValidationIssues(branch, passed, status?.toSet())
                                }
                                .build()
                )
            } else {
                null
            }

    private fun getValidationIssues(branch: Branch, passed: Boolean?, status: Set<String>?): List<GQLTypeValidationIssue.Data> {
        return transactionService.doInTransaction {
            // Gets the issue service for the project
            val issueService = issueServiceExtensionService.getIssueServiceExtension(branch.project)
            if (issueService != null) {
                // Index of issue key --> list of runs
                val index = mutableMapOf<String, MutableList<ValidationRun>>()
                // Gets validation runs for the branch
                val statuses = if (passed != null) {
                    if (passed) {
                        validationRunStatusService.validationRunStatusList.filter { it.isPassed }
                    } else {
                        validationRunStatusService.validationRunStatusList.filter { !it.isPassed }
                    }
                } else {
                    validationRunStatusService.validationRunStatusList.toList()
                }
                val runs = structureService.getValidationRunsForStatus(
                        branch.id,
                        statuses,
                        0,
                        10 // TODO Make this an argument (limit in the past)
                )
                // Loops over all statuses
                runs.forEach { run ->
                    run.validationRunStatuses.forEach { runStatus ->
                        val description = runStatus.description
                        if (!description.isNullOrBlank()) {
                            // Gets the issue keys
                            val keys = issueService.extractIssueKeysFromMessage(description)
                            keys.forEach { key ->
                                val existing = index[key]
                                if (existing != null) {
                                    existing += run
                                } else {
                                    index[key] = mutableListOf(run)
                                }
                            }
                        }
                    }
                }
                // Reducing the list of runs by unique ID
                val reducedIndex = index.mapValues { (_, runs) ->
                    runs.distinctBy { it.id() }
                }
                // Loading of the issues
                reducedIndex.map { (key, runs) ->
                    GQLTypeValidationIssue.Data(
                            issue = issueService.getIssue(key),
                            validationRuns = runs
                    )
                }.filter { data ->
                    status == null || data.issue.status.name in status
                }
            } else {
                emptyList()
            }
        }
    }
}