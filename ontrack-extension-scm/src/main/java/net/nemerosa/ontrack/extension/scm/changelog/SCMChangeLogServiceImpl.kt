package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.scm.service.ProjectNoSCMException
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service

@Service
class SCMChangeLogServiceImpl(
    private val scmDetector: SCMDetector,
) : SCMChangeLogService {

    override suspend fun getChangeLog(from: Build, to: Build): SCMChangeLog {

        if (from.project.id() != to.project.id()) {
            throw SCMChangeLogNotSameProjectException()
        }

        val scm = scmDetector.getSCM(from.project) ?: throw ProjectNoSCMException()
        if (scm !is SCMChangeLogEnabled) {
            throw SCMChangeLogNotEnabledException(from.project.name)
        }

        // Gets the two boundaries
        val fromCommit = scm.getBuildCommit(from)
        val toCommit = scm.getBuildCommit(to)
        if (fromCommit.isNullOrBlank() || toCommit.isNullOrBlank()) {
            throw SCMChangeLogNoCommitException()
        }

        // Getting the list of commits
        val commits = scm.getCommits(fromCommit, toCommit)

        // Getting the issue service
        val configuredIssueService: ConfiguredIssueService? = scm.getConfiguredIssueService()
        val issuesChangeLog: SCMChangeLogIssues? = if (configuredIssueService != null) {
            // Index of issues
            val index = mutableMapOf<String, Issue>()
            // For all commits in this commit log
            commits.forEach { commit ->
                val keys = configuredIssueService.extractIssueKeysFromMessage(commit.message)
                keys.forEach { key ->
                    val exisingIssue = index[key]
                    if (exisingIssue == null) {
                        val issue = configuredIssueService.getIssue(key)
                        if (issue != null) {
                            index[key] = issue
                        }
                    }
                }
            }
            // OK
            val issues = index.values.sortedBy { it.key }
            val issueServiceConfiguration = configuredIssueService.issueServiceConfigurationRepresentation
            SCMChangeLogIssues(
                issueServiceConfiguration,
                issues
            )
        } else {
            null
        }

        // OK
        return SCMChangeLog(
            from = from,
            to = to,
            commits = commits,
            issues = issuesChangeLog,
        )
    }

}