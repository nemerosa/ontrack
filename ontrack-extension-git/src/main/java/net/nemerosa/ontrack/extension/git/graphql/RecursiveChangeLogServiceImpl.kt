package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.common.getOrNull
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

    override fun getDependencyChangeLog(
        buildFrom: Build,
        buildTo: Build,
        depName: String,
    ): Pair<Build, Build>? {
        // Makes sure the order of the builds is correct
        // "From" must be the most recent build
        val from: Build
        val to: Build
        if (buildFrom.signature.time < buildTo.signature.time) {
            from = buildTo
            to = buildFrom
        } else {
            from = buildFrom
            to = buildTo
        }
        // Gets the build just before "to"
        val previous = structureService.getPreviousBuild(to.id).getOrNull()
        // If no previous build, we cannot compute a change log
            ?: return null
        // Gets the dependency builds
        val depCheck = { build: Build -> build.project.name == depName }
        val depFrom = structureService.getBuildsUsedBy(from, filter = depCheck).pageItems.firstOrNull()
        val depTo = structureService.getBuildsUsedBy(previous, filter = depCheck).pageItems.firstOrNull()
        // Only returning the consistent non-null boundaries
        return if (depFrom != null && depTo != null) {
            depFrom to depTo
        } else {
            null
        }
    }

    override fun getBuildByCommit(commitHash: String): Build? =
        propertyService.findByEntityTypeAndSearchArguments(
            entityType = ProjectEntityType.BUILD,
            propertyType = GitCommitPropertyType::class,
            searchArguments = GitCommitPropertyType.getGitCommitSearchArguments(commitHash)
        ).firstOrNull()?.let { id ->
            structureService.getBuild(id)
        }
}