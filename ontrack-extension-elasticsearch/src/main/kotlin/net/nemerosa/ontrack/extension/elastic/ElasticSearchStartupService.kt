package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestClient
import io.searchbox.indices.CreateIndex
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_SERVICE_ELASTICSEARCH
)
class ElasticSearchStartupService(
        private val jestClient: JestClient,
        private val searchProviders: List<SearchProvider>
) : StartupService {

    override fun getName(): String = "Creation of ElasticSearch indexes"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION - 1 // Just before the jobs

    override fun start() {
        searchProviders.forEach { provider ->
            provider.searchIndexers.map { indexer ->
                val action = CreateIndex.Builder(indexer.indexName).build()
                jestClient.execute(action)
            }
        }
    }

}
