package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.ObjectAlreadyExistsException
import net.nemerosa.ontrack.dsl.v4.properties.MainBuildLinks
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the branch DSL
 */
@AcceptanceTestSuite
class ACCDSLBranch extends AbstractACCDSL {

    @Test
    void 'Getting an existing build by name when it exists'() {
        def projectName = uid('P')
        def project = ontrack.project(projectName)
        def branch = project.branch("main")
        def build = branch.build("1")
        // Creating it again
        def again = branch.build("1", "", true)
        // Checking they are the same
        assert again.id == build.id
    }

    @Test
    void 'Getting an existing build by name raises an error when it exists'() {
        def projectName = uid('P')
        def project = ontrack.project(projectName)
        def branch = project.branch("main")
        def build = branch.build("1")
        // Creating it again
        try {
            branch.build("1", "", false)
            fail("Was expecting the build creation to fail")
        } catch (ObjectAlreadyExistsException ignored) {
            // OK
        }
    }

    @Test
    void 'No main build links property'() {
        def projectName = uid('P')
        def project = ontrack.project(projectName)

        // Gets the main build links property
        def mainBuildLinks = project.config.mainBuildLinks
        assert mainBuildLinks == null: "No main build links property is defined."
    }

    @Test
    void 'Main build links property'() {
        def projectName = uid('P')
        def project = ontrack.project(projectName)

        // Sets the main build links property
        project.config.mainBuildLinks = new MainBuildLinks(
                ["my-dependency"],
                true
        )

        // Gets the main build links property
        def mainBuildLinks = project.config.mainBuildLinks
        assert mainBuildLinks.labels == ["my-dependency"]
        assert mainBuildLinks.overrideGlobal: "Override global flag is set to true"
    }

}
