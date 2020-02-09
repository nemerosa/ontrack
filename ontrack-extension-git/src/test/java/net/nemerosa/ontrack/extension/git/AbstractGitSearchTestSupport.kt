package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.model.structure.SearchIndexService
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource

/**
 * Testing the search on Git branches and projects.
 */
@TestPropertySource(
        properties = [
            "ontrack.config.search.engine=elasticsearch",
            "ontrack.config.search.index.immediate=true"
        ]
)
abstract class AbstractGitSearchTestSupport : AbstractGitTestSupport() {

    @Autowired
    protected lateinit var searchIndexService: SearchIndexService

    @Autowired
    protected lateinit var searchService: SearchService


}