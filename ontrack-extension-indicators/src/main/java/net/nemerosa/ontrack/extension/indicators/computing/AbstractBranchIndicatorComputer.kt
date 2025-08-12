package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import kotlin.jvm.optionals.getOrNull

abstract class AbstractBranchIndicatorComputer(
        extension: ExtensionFeature,
        private val structureService: StructureService
) : AbstractExtension(extension), IndicatorComputer {

    override fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>> {
        // Gets the main branch for this project
        val branch = getMainBranch(project)?.takeIf { isBranchEligible(it) }
        // If there is branch, launches the computation
        return if (branch != null) {
            computeIndicators(branch)
        } else {
            emptyList()
        }
    }

    override fun isProjectEligible(project: Project): Boolean =
            getMainBranch(project)?.let {
                isBranchEligible(it)
            } ?: false

    abstract fun isBranchEligible(branch: Branch): Boolean

    abstract fun computeIndicators(branch: Branch): List<IndicatorComputedValue<*, *>>

    private fun getMainBranch(project: Project): Branch? {
        // Sticking to the notion of a `master` branch for now
        // Could be driven by a property later on
        return structureService.findBranchByName(project.name, "master").getOrNull()
    }

}