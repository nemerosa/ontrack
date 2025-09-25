package net.nemerosa.ontrack.extension.scm.service

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.scm.branching.BranchingModelService
import net.nemerosa.ontrack.extension.scm.index.SCMBuildIndexEnabled
import net.nemerosa.ontrack.extension.scm.model.BranchInfo
import net.nemerosa.ontrack.extension.scm.model.BranchInfos
import net.nemerosa.ontrack.model.metrics.time
import net.nemerosa.ontrack.model.metrics.timeNotNull
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class BranchInfoServiceImpl(
    private val scmDetector: SCMDetector,
    private val meterRegistry: MeterRegistry,
    private val branchingModelService: BranchingModelService,
    private val structureService: StructureService,
) : BranchInfoService {

    private val logger: Logger = LoggerFactory.getLogger(BranchInfoServiceImpl::class.java)

    override fun getBranchInfos(
        project: Project,
        commit: String
    ): List<BranchInfos> {

        // SCM interface
        val scm = scmDetector.getSCM(project) ?: return emptyList()

        // Controlling the indexation
        return if (scm is SCMBuildIndexEnabled) {

            // Gets all the branches which contain this commit
            val scmBranches = meterRegistry.timeNotNull(
                METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BRANCHES,
                "project" to project.name,
            ) {
                scm.getBranchesForCommit(commit)
            }

            // Using the branching model to get branch groups
            val indexedBranches = meterRegistry.timeNotNull(
                METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_INDEX,
                "project" to project.name,
            ) {
                branchingModelService.getBranchingModel(project)
                    .groupBranches(scmBranches)
                    .mapValues { (_, groupedScmBranches) ->
                        groupedScmBranches.mapNotNull { scmBranch ->
                            scm.findBranchFromScmBranchName(project, scmBranch)
                        }
                    }
                    .filterValues { it.isNotEmpty() }
            }

            val branchInfos = indexedBranches.mapValues { (_, branches) ->
                branches.map { branch ->
                    // Gets the earliest build on this branch that contains this commit
                    val firstBuild = meterRegistry.time(
                        METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BUILD,
                        "project" to project.name,
                        "branch" to branch.name,
                    ) {
                        scm.findEarliestBuildAfterCommit(
                            branch = branch,
                            commit = commit,
                        )
                    }

                    // Promotions
                    val promotions = meterRegistry.timeNotNull(
                        METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_PROMOTIONS,
                        "project" to project.name,
                        "branch" to branch.name,
                    ) {
                        firstBuild?.let { build ->
                            structureService.getPromotionLevelListForBranch(branch.id)
                                .mapNotNull { promotionLevel ->
                                    structureService.getEarliestPromotionRunAfterBuild(promotionLevel, build)
                                        .orElse(null)
                                }
                        } ?: emptyList()
                    }

                    // Complete branch info
                    BranchInfo(
                        branch = branch,
                        firstBuild = firstBuild,
                        promotions = promotions,
                    )
                }
            }.mapValues { (_, infos) ->
                infos.filter { !it.empty }
            }.filterValues {
                !it.isEmpty()
            }

            // Wrapping into a result
            return branchInfos.map { (type, branchInfoList) ->
                BranchInfos(
                    type = type,
                    branchInfoList = branchInfoList,
                )
            }
        } else {
            logger.warn(
                """
                    Tried to get SCM branch info on ${project.name} but SCM build index 
                    is not enabled
                    (${scm::class.java.name} does not implement ${SCMBuildIndexEnabled::class}).
                """.trimIndent()
            )
            emptyList()
        }
    }

    companion object {
        const val METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BRANCHES = "ontrack_extension_scm_branch_info_branches"
        const val METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_INDEX = "ontrack_extension_scm_branch_info_index"
        const val METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_BUILD = "ontrack_extension_scm_branch_info_build"
        const val METRIC_ONTRACK_EXTENSION_SCM_BRANCH_INFO_PROMOTIONS = "ontrack_extension_scm_branch_info_promotions"
    }

}