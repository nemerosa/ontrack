package net.nemerosa.ontrack.extension.github.ingestion.processing.buildid

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.WorkflowRun
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component

@Component
class CommitBuildIdStrategy(
    private val propertyService: PropertyService,
) : AbstractBuildIdStrategy("commit") {

    override fun findBuild(workflowRun: WorkflowRun, config: JsonNode): Build? {
        TODO("Not yet implemented")
    }

    override fun setupBuild(build: Build, workflowRun: WorkflowRun, config: JsonNode) {
        if (!propertyService.hasProperty(build, GitCommitPropertyType::class.java)) {
            propertyService.editProperty(
                build,
                GitCommitPropertyType::class.java,
                GitCommitProperty(workflowRun.headSha)
            )
        }
    }
}