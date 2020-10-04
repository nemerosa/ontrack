package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.stale.StaleBranchCheck
import net.nemerosa.ontrack.extension.stale.StaleBranchStatus
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Sets branches as stale, and to be disbaled / deleted, when they are linked to a PR which does not
 * exist any longer.
 */
@Component
class PullRequestStaleBranchCheck(
        extensionFeature: GitExtensionFeature,
        private val gitService: GitService,
        private val gitConfigProperties: GitConfigProperties
) : AbstractExtension(extensionFeature), StaleBranchCheck {

    /**
     * Porject must be scanned if it is linked to a Git repository
     */
    override fun isProjectEligible(project: Project): Boolean =
            gitConfigProperties.pullRequests.enabled && gitConfigProperties.pullRequests.cleanup.enabled && gitService.isProjectConfiguredForGit(project)

    /**
     * Branch is eligible if it is a PR.
     */
    override fun isBranchEligible(branch: Branch): Boolean =
            gitConfigProperties.pullRequests.enabled && gitConfigProperties.pullRequests.cleanup.enabled &&
                    gitService.isBranchConfiguredForGit(branch) && gitService.isBranchAPullRequest(branch)

    override fun getBranchStaleness(branch: Branch, lastBuild: Build?): StaleBranchStatus? {
        if (gitConfigProperties.pullRequests.enabled && gitConfigProperties.pullRequests.cleanup.enabled) {
            val pr = gitService.getBranchAsPullRequest(branch)
            if (pr != null) {
                if (pr.isValid) {
                    return null
                } else {
                    val lastTime = lastBuild?.signature?.time ?: branch.signature.time
                    // Current time
                    val now = Time.now()
                    // Disabling time
                    val disablingTime: LocalDateTime = now.minusDays(gitConfigProperties.pullRequests.cleanup.disabling.toLong())
                    // Deletion time
                    val deletionTime: LocalDateTime = disablingTime.minusDays(gitConfigProperties.pullRequests.cleanup.deleting.toLong())
                    // Check
                    return when {
                        lastTime < deletionTime -> StaleBranchStatus.DELETE
                        lastTime < disablingTime -> StaleBranchStatus.DISABLE
                        else -> null
                    }
                }
            } else {
                return null
            }
        } else {
            return null
        }
    }
}