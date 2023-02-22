package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RecursiveChangeLogServiceImpl(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
) : RecursiveChangeLogService {

    override fun getBuildByCommit(commitHash: String): Build? =
        propertyService.findByEntityTypeAndSearchArguments(
            entityType = ProjectEntityType.BUILD,
            propertyType = GitCommitPropertyType::class,
            searchArguments = GitCommitPropertyType.getGitCommitSearchArguments(commitHash)
        ).firstOrNull()?.let { id ->
            structureService.getBuild(id)
        }

    override fun getDepBuildByCommit(commitHash: String, projectName: String): Build? =
        getBuildByCommit(commitHash)?.let { build ->
            structureService.getBuildsUsedBy(build) {
                it.project.name == projectName
            }
        }?.pageItems?.firstOrNull()
}