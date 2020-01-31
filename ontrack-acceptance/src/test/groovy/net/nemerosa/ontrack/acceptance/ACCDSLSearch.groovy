package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.SearchResult
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCDSLSearch extends AbstractACCDSL {

    @Test
    void 'Searching for a build based on its meta information'() {
        def project = uid('P')
        def name = uid('N')
        def value = uid('V')
        ontrack.project(project) {
            branch('test') {
                build('1', 'Build 1') {
                    config {
                        metaInfo name, "${value}-1"
                    }
                }
                build('2', 'Build 2') {
                    config {
                        metaInfo name, "${value}-2"
                    }
                }
            }
        }

        def build1 = ontrack.build(project, 'test', '1')
        def build2 = ontrack.build(project, 'test', '2')

        // Checks that we find two build on exact match but with according scores
        def results = ontrack.search("$name:${value}-1")
        def build1Result = results.find { it.title == "Build ${build1.project}/${build1.branch}/${build1.name}" }
        assert build1Result: "Build 1 found"
        def build2Result = results.find { it.title == "Build ${build2.project}/${build2.branch}/${build2.name}" }
        assert build2Result: "Build 2 found"
        assert build1Result.accuracy > build2Result.accuracy: "Build 1 has a better score than build 2"

        // Checks that we find two builds on prefix match
        results = ontrack.search("$name:$value")
        assert results.find { it.title == "Build ${build1.project}/${build1.branch}/${build1.name}" }: "Build 1 found"
        assert results.find { it.title == "Build ${build2.project}/${build2.branch}/${build2.name}" }: "Build 2 found"

    }

    @Test
    void 'Searching for a build based on its meta information after it has been deleted does not return any result'() {
        def project = uid('P')
        def name = uid('N')
        def value = uid('V')
        ontrack.project(project) {
            branch('test') {
                build('1', 'Build 1') {
                    config {
                        metaInfo name, value
                    }
                }
            }
        }

        def build = ontrack.build(project, 'test', '1')

        // Checks that we find the build
        def results = ontrack.search("$name:$value")
        assert results.find { it.title == "Build ${build.project}/${build.branch}/${build.name}" }: "Build found"

        // Deletes the build
        build.delete()

        // Checks that we do NOT find the build any longer
        def newResults = ontrack.search("$name:$value")
        assert !newResults.find { it.title == "Build ${build.project}/${build.branch}/${build.name}" }: "Build not found"

    }

}
