package net.nemerosa.ontrack.model.buildfilter

import net.nemerosa.ontrack.model.structure.BuildView
import net.nemerosa.ontrack.model.structure.Project

/**
 * Two builds in a branch.
 */
abstract class BuildDiff(
    val project: Project,
) {
    abstract val from: BuildView
    abstract val to: BuildView
}
