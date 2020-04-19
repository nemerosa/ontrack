package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.SearchResult
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCDSLSearch extends AbstractACCDSL {

    @Test
    void 'Searching for a build based on its release information'() {
        def project = uid('P')
        def value = uid('V')
        ontrack.project(project) {
            branch('test') {
                build('1', 'Build 1') {
                    config {
                        label "${value}-1"
                    }
                }
                build('2', 'Build 2') {
                    config {
                        label "${value}-2"
                    }
                }
            }
        }

        def build1 = ontrack.build(project, 'test', '1')
        def build2 = ontrack.build(project, 'test', '2')

        // Checks that we find two build on exact match but with according scores
        def results = ontrack.search("${value}-1")
        def build1Result = results.find { it.title == "Build ${build1.project}/${build1.branch}/${build1.name}" }
        assert build1Result: "Build 1 found"
        def build2Result = results.find { it.title == "Build ${build2.project}/${build2.branch}/${build2.name}" }
        assert build2Result: "Build 2 found"
        assert build1Result.accuracy > build2Result.accuracy: "Build 1 has a better score than build 2"

        // Checks that we find two builds on prefix match
        results = ontrack.search(value)
        assert results.find { it.title == "Build ${build1.project}/${build1.branch}/${build1.name}" }: "Build 1 found"
        assert results.find { it.title == "Build ${build2.project}/${build2.branch}/${build2.name}" }: "Build 2 found"

    }

    @Test
    void 'Searching for a build based on its release information after it has been deleted does not return any result'() {
        def project = uid('P')
        def value = uid('V')
        ontrack.project(project) {
            branch('test') {
                build('1', 'Build 1') {
                    config {
                        label value
                    }
                }
            }
        }

        def build = ontrack.build(project, 'test', '1')

        // Checks that we find the build
        def results = ontrack.search(value)
        assert results.find { it.title == "Build ${build.project}/${build.branch}/${build.name}" }: "Build found"

        // Deletes the build
        build.delete()

        // Checks that we do NOT find the build any longer
        def newResults = ontrack.search(value)
        assert !newResults.find { it.title == "Build ${build.project}/${build.branch}/${build.name}" }: "Build not found"

    }

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

    @Test
    void 'Searching for a branch based on its meta information'() {
        def project = uid('P')
        def name = uid('N')
        def value = uid('V')
        ontrack.project(project) {
            branch('test-1') {
                config {
                    metaInfo name, "${value}-1"
                }
            }
            branch('test-2') {
                config {
                    metaInfo name, "${value}-2"
                }
            }
        }

        def branch1 = ontrack.branch(project, 'test-1')
        def branch2 = ontrack.branch(project, 'test-2')

        // Checks that we find two builds on exact match but with according scores
        def results = ontrack.search("$name:${value}-1")
        def branch1Result = results.find { it.title == "Branch ${branch1.project}/${branch1.name}" }
        assert branch1Result: "Branch 1 found"
        def branch2Result = results.find { it.title == "Branch ${branch2.project}/${branch2.name}" }
        assert branch2Result: "Branch 2 found"
        assert branch1Result.accuracy > branch2Result.accuracy: "Branch 1 has a better score than branch 2"

        // Checks that we find two branches on prefix match
        results = ontrack.search("$name:$value")
        assert results.find { it.title == "Branch ${branch1.project}/${branch1.name}" }: "Branch 1 found"
        assert results.find { it.title == "Branch ${branch2.project}/${branch2.name}" }: "Branch 2 found"

    }

    @Test
    void 'Searching for a branch based on its meta information after it has been deleted does not return any result'() {
        def project = uid('P')
        def name = uid('N')
        def value = uid('V')
        ontrack.project(project) {
            branch('test') {
                config {
                    metaInfo name, value
                }
            }
        }

        def branch = ontrack.branch(project, 'test')

        // Checks that we find the branch
        def results = ontrack.search("$name:$value")
        assert results.find { it.title == "Branch ${branch.project}/${branch.name}" }: "Branch found"

        // Deletes the branch
        branch.delete()

        // Checks that we do NOT find the branch any longer
        def newResults = ontrack.search("$name:$value")
        assert !newResults.find { it.title == "Branch ${branch.project}/${branch.name}" }: "Branch not found"

    }

    @Test
    void 'Searching for a project based on its meta information'() {
        def project1Name = uid('P')
        def project2Name = uid('P')
        def name = uid('N')
        def value = uid('V')
        ontrack.project(project1Name) {
            config {
                metaInfo name, "${value}-1"
            }
        }
        ontrack.project(project2Name) {
            config {
                metaInfo name, "${value}-2"
            }
        }

        // Checks that we find two projects on exact match but with according scores
        def results = ontrack.search("$name:${value}-1")
        def project1Result = results.find { it.title == "Project ${project1Name}" }
        assert project1Result: "Project 1 found"
        def project2Result = results.find { it.title == "Project ${project2Name}" }
        assert project2Result: "Project 2 found"
        assert project1Result.accuracy > project2Result.accuracy: "Project 1 has a better score than project 2"

        // Checks that we find two projects on prefix match
        results = ontrack.search("$name:$value")
        assert results.find { it.title == "Project ${project1Name}" }: "Project 1 found"
        assert results.find { it.title == "Project ${project2Name}" }: "Project 2 found"

    }

    @Test
    void 'Searching for a project based on its meta information after it has been deleted does not return any result'() {
        def projectName = uid('P')
        def name = uid('N')
        def value = uid('V')
        def project = ontrack.project(projectName) {
            config {
                metaInfo name, value
            }
        }

        // Checks that we find the project
        def results = ontrack.search("$name:$value")
        assert results.find { it.title == "Project ${projectName}" }: "Project found"

        // Deletes the project
        project.delete()

        // Checks that we do NOT find the project any longer
        def newResults = ontrack.search("$name:$value")
        assert !newResults.find { it.title == "Project ${projectName}" }: "Project not found"

    }

    @Test
    void 'Searching and paginating for builds based on their release information'() {
        def projectName = uid('P')
        def prefix = uid('v')
        // Creates 25 builds
        ontrack.project(projectName) {
            branch("master") {
                (1..25).each { no ->
                    build("$no") {
                        config {
                            label("$prefix-$no")
                        }
                    }
                }
            }
        }
        // Looking for the prefix
        List<SearchResult> results = ontrack.search(prefix, "build-release", 10, 5)
        // Checks the results
        assert results.size() == 5
    }

}
