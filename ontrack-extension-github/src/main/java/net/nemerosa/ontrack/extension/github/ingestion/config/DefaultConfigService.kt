package net.nemerosa.ontrack.extension.github.ingestion.config

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultConfigService : ConfigService {

    override fun saveConfig(owner: String, repository: String, branch: String, path: String) {
        TODO("Not yet implemented")
    }

    override fun removeConfig(owner: String, repository: String, branch: String) {
        TODO("Not yet implemented")
    }

}