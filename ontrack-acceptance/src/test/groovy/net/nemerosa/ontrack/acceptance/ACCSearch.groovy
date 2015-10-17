package net.nemerosa.ontrack.acceptance

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.json.JsonUtils.array
import static net.nemerosa.ontrack.json.JsonUtils.object

/**
 * Search acceptance tests.
 */
@AcceptanceTestSuite
class ACCSearch extends AcceptanceTestClient {

    @Test
    void 'Looking for a build when anonymous does not return anything by default'() {
        // Prerequisites
        JsonNode build = doCreateBuild()
        // Looking for this build as anonymous
        def results = anonymous().post(
                object()
                        .with('token', build.path('name').asText())
                        .end(),
                'search'
        ).get()
        // Check
        assert results == array().end()
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
        def results = admin().post(
                object()
                        .with('token', build.path('name').asText())
                        .end(),
                'search'
        ).get()
        // Check
        def result = results.get(0)
        assert result.path('title').asText() == "Build ${project}/${branch}/${name}" as String
        assert result.path('page').asText() == "${baseURL}/#/build/${id}" as String
    }

}
