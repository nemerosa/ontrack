package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.json.JsonUtils.object

/**
 * Acceptance tests for the GraphQL interface
 */
@AcceptanceTestSuite
class ACCDSLGraphQL extends AbstractACCDSL {

    @Test
    void 'List of branches using GraphQL'() {
        def project = doCreateProject()
        def projectName = project.name.asText() as String
        def projectId = project.id.asInt() as int

        doCreateBranch(projectId, object().with('name', '1.0').with('description', '').end())
        doCreateBranch(projectId, object().with('name', '2.0').with('description', '').end())

        def result = ontrack.graphQLQuery("""{
            projects(name: "${projectName}") {
                id
                branches {
                    name
                }
            }
        }""")

        assert result.errors != null && result.errors.empty
        assert result.data.projects.size() == 1
        assert result.data.projects.get(0).id == projectId
        assert result.data.projects.get(0).branches.size() == 2
        assert result.data.projects.get(0).branches*.name as Set == ['1.0', '2.0'] as Set
    }

}
