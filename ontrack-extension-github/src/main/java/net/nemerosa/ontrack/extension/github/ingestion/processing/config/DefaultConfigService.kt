package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.EntityDataService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultConfigService(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val entityDataService: EntityDataService,
    private val configLoaderService: ConfigLoaderService,
) : ConfigService {

    override fun getOrLoadConfig(repository: Repository, branch: String, path: String): IngestionConfig {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not supported yet
        )
        return load(ontrackBranch)
            ?: configLoaderService.loadConfig(ontrackBranch, path)
                ?.apply { store(ontrackBranch) } // Stores when loaded
            ?: IngestionConfig() // Default configuration
    }

    override fun saveConfig(repository: Repository, branch: String, path: String): IngestionConfig? {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not supported yet
        )
        val config = configLoaderService.loadConfig(ontrackBranch, path)
        return config?.apply {
            store(ontrackBranch)
        }
    }

    private fun IngestionConfig.store(ontrackBranch: Branch) {
        entityDataService.store(
            ontrackBranch,
            IngestionConfig::class.java.name,
            this,
        )
    }

    override fun removeConfig(repository: Repository, branch: String) {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not supported yet
        )
        entityDataService.delete(
            ontrackBranch,
            IngestionConfig::class.java.name,
        )
    }

    override fun findConfig(repository: Repository, branch: String): IngestionConfig? {
        val ontrackBranch = ingestionModelAccessService.getOrCreateBranch(
            repository = repository,
            headBranch = branch,
            baseBranch = null, // TODO PRs not supported yet
        )
        return load(ontrackBranch)
    }

    private fun load(ontrackBranch: Branch) = entityDataService.retrieve(
        ontrackBranch,
        IngestionConfig::class.java.name,
        IngestionConfig::class.java,
    )
}