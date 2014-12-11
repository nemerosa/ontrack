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

        // Creating some some promotion levels using the DSL
        def promotions = ['COPPER', 'BRONZE', 'GOLD']

        promotions.each { branch.promotionLevel(it, "$it promotion") }

        // Creating some builds
        def builds = (1..3).collect { branch.build("$it", "Build $it") }

        // Promoting the builds
        (0..2).each { builds[it].promote(promotions[it]) }

        // Getting the last promoted builds
        def results = branch.lastPromotedBuilds
        assert results.size() == 3
        assert results[0].name == '1'
        assert results[1].name == '2'
        assert results[2].name == '3'
    }

}
