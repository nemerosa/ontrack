package net.nemerosa.ontrack.extension.issues.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.issues.IssueServiceExtensionService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLTypeValidationRunStatus
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
import org.springframework.stereotype.Component

@Component
class ValidationRunStatusIssuesGraphQLFieldContributor(
        private val issue: GQLTypeIssue,
        private val structureService: StructureService,
        private val issueServiceExtensionService: IssueServiceExtensionService
) : GQLFieldContributor {

    override fun getFields(type: Class<*>): List<GraphQLFieldDefinition> =
            if (ValidationRunStatus::class.java.isAssignableFrom(type)) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("issues")
                                .description("List of issues attached to this status")
                                .type(stdList(issue.typeRef))
                                .argument {
                                    it.name("status")
                                            .description("Filtering the issues on their status")
                                            .type(GraphQLString)
                                }
                                .dataFetcher { env ->
                                    val validationRunStatus: GQLTypeValidationRunStatus.Data = env.getSource()
                                    val status: String? = env.getArgument("status")
                                    val issues = getIssueList(validationRunStatus.delegate)
                                    issues.filter {
                                        status.isNullOrBlank() || it.status.name == status
                                    }
                                }
                                .build()
                )
            } else {
                emptyList()
            }

    private fun getIssueList(validationRunStatus: ValidationRunStatus): List<Issue> {
        // Status description
        val description = validationRunStatus.description
        // If no description, no issue
        return if (description.isNullOrBlank()) {
            emptyList()
        }
        // We need an IssueServiceExtension to parse the message
        else {
            // Gets the validation run
            val validationRun = structureService.getParentValidationRun(validationRunStatus.id)
            // Gets the configured issue service for this project
            val issueService = validationRun?.run { issueServiceExtensionService.getIssueServiceExtension(project) }
            // If no issue service is configured, no issue
            issueService
                    // Getting the issues from the description
                    ?.extractIssueKeysFromMessage(description)
                    ?.map { key ->
                        issueService.getIssue(key)
                    }
                    ?: emptyList()
        }
    }
}