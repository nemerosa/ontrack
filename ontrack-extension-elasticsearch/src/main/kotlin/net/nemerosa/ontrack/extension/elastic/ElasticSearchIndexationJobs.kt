package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.SearchIndexer
import net.nemerosa.ontrack.model.structure.SearchItem
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.support.JobProvider
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * One job per type of search.
 */
@Component
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_SERVICE_ELASTICSEARCH
)
class ElasticSearchIndexationJobs(
        private val providers: List<SearchProvider>,
        private val elasticSearchService: ElasticSearchService
) : JobProvider {

    private val jobCategory = JobCategory("elasticsearch", "ElasticSearch")
    private val indexationJobType = jobCategory.getType("indexation").withName("Search indexation")

    override fun getStartingJobs(): Collection<JobRegistration> =
            providers.flatMap { provider ->
                provider.searchIndexers.map { indexer ->
                    createIndexationJobRegistration(indexer)
                }
            }

    private fun <T: SearchItem> createIndexationJobRegistration(searchIndexer: SearchIndexer<T>) = JobRegistration(
            job = createIndexationJob(searchIndexer),
            schedule = searchIndexer.schedule
    )

    private fun <T: SearchItem> createIndexationJob(indexer: SearchIndexer<T>) = object : Job {
        override fun isDisabled(): Boolean = indexer.isDisabled

        override fun getKey(): JobKey =
                indexationJobType.getKey(indexer.id)

        override fun getDescription(): String = indexer.name

        override fun getTask() = JobRun { listener ->
            listener.message("Launching indexation for ${indexer.name}")
            elasticSearchService.index(indexer)
        }
    }

}