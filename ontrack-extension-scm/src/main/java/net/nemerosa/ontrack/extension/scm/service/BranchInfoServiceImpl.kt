package net.nemerosa.ontrack.extension.scm.service

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.scm.model.BranchInfos
import net.nemerosa.ontrack.model.metrics.timeNotNull
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service

@Service
class BranchInfoServiceImpl(
    private val scmDetector: SCMDetector,
    private val meterRegistry: MeterRegistry,
    // TODO private val branchingModelService: BranchingModelService,
) : BranchInfoService {

    override fun getBranchInfos(
        project: Project,
        commit: String
    ): List<BranchInfos> {

        // SCM interface
        val scm = scmDetector.getSCM(project) ?: return emptyList()

        // Gets all the branches which contain this commit
        val branches = meterRegistry.timeNotNull(
            METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BRANCHES,
            "project" to project.name,
        ) {
            scm.getBranchesForCommit(commit)
        }

        TODO("Not yet implemented")
    }

    companion object {
        const val METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BRANCHES = "ontrack_extension_scm_branch_info_branches"
    }

}