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
    void 'Looking for a project after its creation'() {
        // Prerequisites
        JsonNode node = doCreateProject()
        // Data
        String project = node.path('name').asText()
        String id = node.path('id').asText()
        // Looking for this project as admin
        def results = ontrack.search(project)
        // Check
        assert results.size() > 0: "At least one result is returned"
        def result = results.get(0)
        assert result.title == "Project ${project}" as String
        assert result.page == "/#/project/${id}" as String
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
        assert results.size() > 0: "At least one result is returned"
        def result = results.get(0)
        assert result.title == "Build ${project}/${branch}/${name}" as String
        assert result.page == "/#/build/${id}" as String
    }

}
