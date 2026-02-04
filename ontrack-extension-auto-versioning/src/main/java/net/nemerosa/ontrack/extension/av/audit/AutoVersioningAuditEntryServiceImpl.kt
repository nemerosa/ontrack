package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningAuditEntryServiceImpl(
    private val scmDetector: SCMDetector,
) : AutoVersioningAuditEntryService {

    override fun getPullRequest(entry: AutoVersioningAuditEntry): SCMPullRequest? {
        val prName = entry.mostRecentState.data[AutoVersioningAuditEntryStateDataKeys.PR_NAME]
            ?.takeIf { it.isNotBlank() }
            ?: return null
        // Target project
        val project = entry.order.branch.project
        // SCM of the project
        val scm = scmDetector.getSCM(project) ?: return null
        // Getting the PR
        return scm.getPullRequestByName(prName)
    }
}