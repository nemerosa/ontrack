package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.properties.MainBuildLinks
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the project DSL
 */
@AcceptanceTestSuite
class ACCDSLProject extends AbstractACCDSL {

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
