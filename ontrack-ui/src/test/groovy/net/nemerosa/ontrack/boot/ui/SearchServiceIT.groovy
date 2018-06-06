package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SearchServiceIT extends AbstractWebTestSupport {

    @Autowired
    private SearchService searchService

    @Test
    void 'Looking for a build'() {
        def build = doCreateBuild()
        def results = asUser().call { searchService.search(new SearchRequest(build.name)) }
        assert results.size() == 1
        assert results[0].title == "Build ${build.project.name}/${build.branch.name}/${build.name}" as String
    }

    @Test
    void 'Looking for a build which is authorised'() {
        def build = doCreateBuild()
        grantViewToAll false
        try {
            def results = asUser().withView(build).call { searchService.search(new SearchRequest(build.name)) }
            assert results.size() == 1
            assert results[0].title == "Build ${build.project.name}/${build.branch.name}/${build.name}" as String
        } finally {
            grantViewToAll true
        }
    }

    @Test
    void 'Looking for a build which is not authorised'() {
        def build = doCreateBuild()
        grantViewToAll false
        try {
            def results = asUser().call { searchService.search(new SearchRequest(build.name)) }
            assert results.empty
        } finally {
            grantViewToAll true
        }
    }

}
