package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import net.nemerosa.ontrack.model.structure.EntityDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultConfigService(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val entityDataService: EntityDataService,
) : ConfigService {

    override fun saveConfig(repository: Repository, branch: String, path: String) {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not suported yet
        )
        TODO("Downloads the configuration")
        TODO("Saves the configuration at branch level")
    }

    override fun removeConfig(repository: Repository, branch: String) {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not suported yet
        )
        entityDataService.delete(
            ontrackBranch,
            IngestionConfig::class.java.name,
        )
    }
}