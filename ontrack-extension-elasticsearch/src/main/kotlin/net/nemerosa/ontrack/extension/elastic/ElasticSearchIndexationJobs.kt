package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.extension.elastic.ElasticSearchJobs.indexationAllJobKey
import net.nemerosa.ontrack.extension.elastic.ElasticSearchJobs.indexationJobType
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.structure.SearchIndexService
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
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchIndexationJobs(
        private val providers: List<SearchProvider>,
        private val elasticSearchService: SearchIndexService,
        private val jobScheduler: JobScheduler
) : JobProvider {


    override fun getStartingJobs(): Collection<JobRegistration> =
            providers.flatMap { provider ->
                provider.searchIndexers.map { indexer ->
                    createIndexationJobRegistration(indexer)
                }
            } + createGlobalIndexationJob()

    private fun createGlobalIndexationJob() = JobRegistration(
            schedule = Schedule.NONE,
            job = object : Job {
                override fun isDisabled(): Boolean = false

                override fun getKey(): JobKey = indexationAllJobKey

                override fun getDescription(): String = "All re-indexations"

                override fun getTask() = JobRun { listener ->
                    listener.message("Launching all indexations")
                    providers.flatMap { provider ->
                        provider.searchIndexers.map { indexer ->
                            val key = indexationJobType.getKey(indexer.indexerId)
                            jobScheduler.fireImmediately(key)
                        }
                    }
                }
            }
    )

    private fun <T : SearchItem> createIndexationJobRegistration(searchIndexer: SearchIndexer<T>) = JobRegistration(
            job = createIndexationJob(searchIndexer),
            schedule = searchIndexer.indexerSchedule
    )

    private fun <T : SearchItem> createIndexationJob(indexer: SearchIndexer<T>) = object : Job {
        override fun isDisabled(): Boolean = indexer.isIndexerDisabled

        override fun getKey(): JobKey =
                indexationJobType.getKey(indexer.indexerId)

        override fun getDescription(): String = indexer.indexerName

        override fun getTask() = JobRun { listener ->
            listener.message("Launching indexation for ${indexer.indexerName}")
            elasticSearchService.index(indexer)
        }
    }

}