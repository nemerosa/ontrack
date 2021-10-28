package net.nemerosa.ontrack.extension.github.ingestion.processing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.ontrackProjectName
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService

abstract class AbstractWorkflowIngestionEventProcessor<T : AbstractWorkflowPayload>(
    protected val structureService: StructureService,
) : AbstractIngestionEventProcessor<T>() {

    protected fun getOrCreateBuild(payload: T): Build {
        // Gets or creates the project
        val project = getOrCreateProject(payload)
        // TODO Branch creation & setup
        // TODO Build creation & setup
    }

    private fun getOrCreateProject(payload: T): Project {
        val name = payload.repository.ontrackProjectName
        return structureService.findProjectByName(name)
            .getOrNull()
            ?: structureService.newProject(
                Project.of(
                    NameDescription.nd(
                        name = name,
                        description = payload.repository.description,
                    )
                )
            )
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AbstractWorkflowPayload(
    val repository: Repository,
)
