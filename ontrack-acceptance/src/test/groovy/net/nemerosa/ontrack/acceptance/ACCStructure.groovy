package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.json.JsonUtils.object
import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCStructure extends AcceptanceTestClient {

    @Test
    void 'No name for a project is invalid'() {
        validationMessage({ doCreateProject(object().end()) }, "The name is required.")
    }

    @Test
    void 'Empty name for a project is invalid'() {
        validationMessage({
            doCreateProject(object().with('name', '').end())
        }, 'The name can only have letters, digits, dots (.), dashes (-) or underscores (_).')
    }

    @Test
    void 'Name validation for a build (correct)'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch.path('id').asInt(), object().with('name', '2.0.0-alpha-1-14').with('description', '').end())
        assert build.path('id').asInt() > 0
    }

    @Test
    void 'Name validation for a branch (correct)'() {
        def project = doCreateProject()
        def branch = doCreateBranch(project.path('id').asInt(), object().with('name', '2.0.0-alpha-x').with('description', '').end())
        assert branch.path('id').asInt() > 0
    }

    @Test
    void 'Name validation for a project (correct)'() {
        def project = doCreateProject(nameDescription())
        assert project.path('id').asInt() > 0
    }

    @Test
    void 'Branch name with an extension'() {
        // Creates a branch with a ".js" as the end of its name
        def projectName = uid('P')
        def branchName = "bugfix-PRJ-678-some-file.js"
        ontrack.project(projectName) {
            branch(branchName)
        }
        // Tries to access the branch
        def branch = ontrack.branch(projectName, branchName)
        assert branch.name == branchName
    }

}
