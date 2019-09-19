package net.nemerosa.ontrack.acceptance

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

/**
 * Search acceptance tests.
 */
@AcceptanceTestSuite
class ACCSearch extends AcceptanceTestClient {

    @Test
    void 'With default security settings, all builds are accessible'() {
        // Prerequisites
        JsonNode build = doCreateBuild()
        // Looking for this build as a different user
        def results = anonymousOntrack.search(build.path('name').asText())
        // Check
        assert results.size() == 1
    }

    @Test
    void 'Looking for a non authorised build when does not return anything'() {
        withNotGrantProjectViewToAll {
            // Prerequisites
            JsonNode build = doCreateBuild()
            // Looking for this build as a different user
            def results = ontrackAsAnyUser.search(build.path('name').asText())
            // Check
            assert results.empty
        }
    }

    @Test
    void 'Looking for a build after its creation'() {
        // Prerequisites
        JsonNode build = doCreateBuild()
        // Data
        String project = build.path('branch').path('project').path('name').asText()
        String branch = build.path('branch').path('name').asText()
        String name = build.path('name').asText()
        String id = build.path('id').asText()
        // Looking for this build as admin
        def results = ontrack.search(build.path('name').asText())
        // Check
        def result = results.get(0)
        assert result.title == "Build ${project}/${branch}/${name}" as String
        assert result.page == "/#/build/${id}" as String
    }

}
