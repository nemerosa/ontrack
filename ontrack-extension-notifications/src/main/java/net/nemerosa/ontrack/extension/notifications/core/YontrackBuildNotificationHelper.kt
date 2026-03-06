package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class YontrackBuildNotificationHelper(
    private val structureService: StructureService,
    private val buildDisplayNameService: BuildDisplayNameService,
) {

    /**
     * Gets a build from the context of an event.
     */
    fun getBuild(
        event: Event,
        projectName: String?,
        branchName: String?,
        buildName: String?,
    ): Build =
        if (buildName.isNullOrBlank()) {
            event.getEntity(ProjectEntityType.BUILD)
        } else if (!branchName.isNullOrBlank()) {
            val branch = getBranch(
                event = event,
                projectName = projectName,
                branchName = branchName,
            )
            structureService.findBuildByName(
                project = branch.project.name,
                branch = branch.name,
                build = buildName
            ).getOrNull()
                ?: buildDisplayNameService.findBuildByDisplayName(
                    project = branch.project,
                    name = buildName,
                    onlyDisplayName = true
                )
                ?: throw BuildNotFoundException(
                    branch.project.name,
                    branch.name,
                    buildName
                )
        } else {
            val project = getProject(event, projectName)
            buildDisplayNameService.findBuildByDisplayName(
                project = project,
                name = buildName,
                onlyDisplayName = false,
            ) ?: throw BuildNotFoundException(
                project.name,
                buildName
            )
        }

    /**
     * Gets a branch from the context of an event.
     */
    fun getBranch(
        event: Event,
        projectName: String?,
        branchName: String?,
    ): Branch =
        if (branchName.isNullOrBlank()) {
            event.getEntity(ProjectEntityType.BRANCH)
        } else {
            val project = getProject(event, projectName)
            structureService.findBranchByName(project.name, branchName).getOrNull()
                ?: throw BranchNotFoundException(project.name, branchName)
        }

    /**
     * Gets a project from the context of an event.
     */
    private fun getProject(
        event: Event,
        projectName: String?,
    ): Project =
        if (projectName.isNullOrBlank()) {
            event.getEntity(ProjectEntityType.PROJECT)
        } else {
            structureService.findProjectByName(projectName).getOrNull()
                ?: throw ProjectNotFoundException(projectName)
        }

}