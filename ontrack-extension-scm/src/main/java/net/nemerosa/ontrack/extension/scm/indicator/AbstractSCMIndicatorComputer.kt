package net.nemerosa.ontrack.extension.scm.indicator

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedValue
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputer
import net.nemerosa.ontrack.extension.scm.service.SCMService
import net.nemerosa.ontrack.extension.scm.service.SCMServiceDetector
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Project

/**
 * [Indicator][IndicatorComputer] which relies on files in the SCM to compute its value.
 *
 * Its relies on the default or main branch to be known.
 */
abstract class AbstractSCMIndicatorComputer(
    extension: ExtensionFeature,
    protected val scmServiceDetector: SCMServiceDetector
) : AbstractExtension(extension), IndicatorComputer {

    /**
     * Parallelisation of the computation is enabled.
     */
    override val perProject: Boolean = true

    /**
     * A project is eligible only if it has a SCM service associated with it.
     */
    override fun isProjectEligible(project: Project): Boolean {
        // Gets the SCM service if any
        val scmService = scmServiceDetector.getScmService(project).getOrNull()
            ?: return false // No SCM ==> not eligible
        // Gets the default branch
        val scmBranch = scmService.getSCMDefaultBranch(project)
        // No default branch ==> not eligible
        return !scmBranch.isNullOrBlank()
    }

    override fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>> {
        val scmService = scmServiceDetector.getScmService(project).getOrNull()
        val scmBranch = scmService?.getSCMDefaultBranch(project)
        return if (scmService != null && scmBranch != null) {
            computeSCMIndicators(project, scmService, scmBranch)
        } else {
            emptyList()
        }
    }

    abstract fun computeSCMIndicators(
        project: Project,
        scmService: SCMService,
        scmBranch: String
    ): List<IndicatorComputedValue<*, *>>
}