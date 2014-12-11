package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import org.junit.Before
import org.junit.Test

/**
 * Ontrack DSL tests.
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSL extends AcceptanceTestClient {

    private Ontrack ontrack

    @Before
    void init() {
        ontrack = OntrackConnection.create(baseURL)
                .disableSsl(sslDisabled)
                .authenticate('admin', adminPassword)
                .build()
    }

    @Test(expected = ClientNotFoundException)
    void 'Branch not authorised'() {
        // Creating a branch
        def testBranch = doCreateBranch()
        // Anonymous client
        ontrack = OntrackConnection.create(baseURL).disableSsl(sslDisabled).build()
        // Branch cannot be found
        ontrack.branch(testBranch.project.name.asText(), testBranch.name.asText())
    }

    @Test
    void 'Getting last promoted build'() {
        // Creating a branch
        def testBranch = doCreateBranch()
        def projectName = testBranch.project.name.asText()
        def branchName = testBranch.name.asText()

        // Gets the branch
        def branch = ontrack.branch(projectName, branchName)
        assert branch.id > 0
        assert branch.project == projectName
        assert branch.name == branchName

        // Creating some some promotion levels using the DSL
        def copper = branch.promotionLevel('COPPER', 'Copper promotion')
        def bronze = branch.promotionLevel('BRONZE', 'Bronze promotion')
        def gold = branch.promotionLevel('GOLD', 'Gold promotion')

        // Creating some builds
        def build1 = branch.build('1', 'Build 1')
        def build2 = branch.build('2', 'Build 2')
        def build3 = branch.build('3', 'Build 3')

        // Promoting the builds
        build1.promote(gold)
        build2.promote(bronze)
        build3.promote(copper)

        // Getting the last promoted builds
        def results = branch.lastPromotedBuilds
        assert results.collect { it.name } == ['3', '2', '1']

    }

}
