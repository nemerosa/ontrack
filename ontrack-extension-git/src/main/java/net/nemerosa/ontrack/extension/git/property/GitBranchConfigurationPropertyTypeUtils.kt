package net.nemerosa.ontrack.extension.git.property

import net.nemerosa.ontrack.model.structure.*

object GitBranchConfigurationPropertyTypeUtils {

    fun findBranchFromScmBranchName(
        propertyService: PropertyService,
        structureService: StructureService,
        project: Project,
        scmBranch: String
    ): Branch? =
        propertyService.findByEntityTypeAndSearchArguments(
            entityType = ProjectEntityType.BRANCH,
            propertyType = GitBranchConfigurationPropertyType::class,
            searchArguments = GitBranchConfigurationProperty.getSearchArguments(scmBranch)
        ).map { branchId ->
            structureService.getBranch(branchId)
        }.firstOrNull { branch ->
            branch.project.id == project.id
        }

}