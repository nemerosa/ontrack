package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchStartupService(
        private val searchService: SearchService
) : StartupService {

    override fun getName(): String = "Creation of ElasticSearch indexes"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION - 1 // Just before the jobs

    override fun start() {
        searchService.indexInit()
    }

}
