package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.SearchResult
import net.nemerosa.ontrack.dsl.http.OTNotFoundException
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the build links
 */
@AcceptanceTestSuite
class ACCDSLBuildLinks extends AbstractACCDSL {

    @Test
    void 'Build link search'() {
        // Creating two builds with some build links
        def p1 = uid('P1')
        def p2 = uid('P2')

        ontrack.project(p2) {
            branch('B2') {
                build('2.0')
                build('3.0')
            }
        }

        ontrack.project(p1) {
            branch('B1') {
                build('1.1') {
                    buildLink p2, '2.0'
                }
                build('1.2') {
                    buildLink p2, '3.0'
                }
            }
        }

        // Build id
        def id = ontrack.build(p1, 'B1', '1.1').id
        // Performs a search
        def results = ontrack.search("${p2}:2*")
        // Gets the results
        assert results.size() == 1
        SearchResult result = results[0]
        assert result.title == "Build ${p1}/B1/1.1"
        assert result.description == "${p1} -> 1.1"
        assert result.uri == "${baseURL}/structure/builds/${id}"
        assert result.page == "${baseURL}/#/build/${id}"
    }

    @Test
    void 'Build links'() {

        // Creating projects, branches and builds

        def p1 = uid('P1')
        ontrack.project(p1) {
            branch('B1', '') {
                build('1.0', '')
                build('1.1', '')
            }
        }

        def p2 = uid('P2')
        ontrack.project(p2) {
            branch('B2', '') {
                build('2.0', '')
                build('2.1', '')
            }
        }

        // Build ids

        def b111 = ontrack.build(p1, 'B1', '1.1').id
        def b220 = ontrack.build(p2, 'B2', '2.0').id

        // Links
        ontrack.build(p1, 'B1', '1.0').with {
            // Same project
            buildLink p1, '1.1'
            // Other project
            buildLink p2, '2.0'
        }

        // Gets the links
        def buildLinks = ontrack.build(p1, 'B1', '1.0').buildLinks

        def collect = buildLinks.collect { [it.project, it.name, it.page] }
        println collect
        assert collect as Set == [
                [p1, '1.1', "${baseURL}/#/build/${b111}"],
                [p2, '2.0', "${baseURL}/#/build/${b220}"],
        ] as Set

    }

    @Test(expected = OTNotFoundException)
    void 'Build links with unexisting build'() {

        // Creating projects, branches and builds

        def p1 = uid('P1')
        ontrack.project(p1) {
            branch('B1', '') {
                build('1.0', '')
                build('1.1', '')
            }
        }

        def p2 = uid('P2')
        ontrack.project(p2) {
            branch('B2', '')
        }

        // Build ids

        def b111 = ontrack.build(p1, 'B1', '1.1').id
        def b220 = ontrack.build(p2, 'B2', '2.0').id

        // Links
        ontrack.build(p1, 'B1', '1.0').with {
            // Unexisting build
            buildLink p2, '2.2'
        }

    }

    @Test(expected = OTNotFoundException)
    void 'Build link with unexisting project'() {

        // Creating projects, branches and builds

        def p1 = uid('P1')
        ontrack.project(p1) {
            branch('B1', '') {
                build('1.0', '')
                build('1.1', '')
            }
        }

        def p3 = uid('P3')

        // Links
        ontrack.build(p1, 'B1', '1.0').with {
            // Unexisting project
            buildLink p3, '3.0'
        }

    }

}
