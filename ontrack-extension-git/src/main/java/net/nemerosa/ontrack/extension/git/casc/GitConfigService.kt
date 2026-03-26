package net.nemerosa.ontrack.extension.git.casc

import net.nemerosa.ontrack.git.support.GitConnectionConfig
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GitConfigService(
    private val storageService: StorageService,
) {

    /**
     * Gets the Git configuration.
     */
    val gitConnectionConfig: GitConnectionConfig
        get() = storageService.find(
            store = STORE_NAME,
            key = GitConnectionConfig::class.java.simpleName,
            type = GitConnectionConfig::class
        ) ?: GitConnectionConfig.default

    /**
     * Saving the Git configuration.
     */
    fun saveGitConnectionConfig(config: GitConnectionConfig) {
        storageService.store(
            store = STORE_NAME,
            key = GitConnectionConfig::class.java.simpleName,
            data = config,
        )
    }

    companion object {
        private val STORE_NAME = GitConfigService::class.java.simpleName
    }

}