package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch"
        ]
)
abstract class AbstractSearchTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var searchService: SearchService

    @Autowired
    protected lateinit var searchIndexService: SearchIndexService

    @Autowired
    protected lateinit var providers: List<SearchProvider>

    protected fun index(indexName: String) {
        providers.forEach { provider ->
            provider.searchIndexers.forEach { indexer ->
                if (indexer.indexName == indexName) {
                    searchIndexService.index(indexer)
                }
            }
        }
    }

}
