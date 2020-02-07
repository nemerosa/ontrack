package net.nemerosa.ontrack.boot.search

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch",
            "ontrack.config.search.index.immediate=true"
        ]
)
abstract class AbstractSearchTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var searchService: SearchService

    @Autowired
    protected lateinit var searchIndexService: SearchIndexService

    @Autowired
    protected lateinit var searchIndexers: List<SearchIndexer<*>>

    protected fun index(indexName: String) {
        asAdmin {
            searchIndexers.forEach { indexer ->
                if (indexer.indexName == indexName) {
                    searchIndexService.index(indexer)
                }
            }
        }
    }

    protected fun Build.release(value: String) {
        setProperty(this, ReleasePropertyType::class.java, ReleaseProperty(value))
    }

}
