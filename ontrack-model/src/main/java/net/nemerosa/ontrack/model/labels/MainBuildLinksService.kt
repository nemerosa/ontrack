package net.nemerosa.ontrack.model.labels

import net.nemerosa.ontrack.model.structure.Project

/**
 * Given a project, returns the list of project labels
 * identifying the build links which should be displayed
 * in the build links decoration.
 */
interface MainBuildLinksService {
    fun getMainBuildLinksConfig(project: Project): MainBuildLinksConfig
}