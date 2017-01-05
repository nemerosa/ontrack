package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class ValidationRunsPaginationQLIT extends AbstractQLITSupport {

    private Branch branch

    @Before
    void setup() {
        branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, nd('VS', ''))
        (1..100).each { buildNo ->
            def build = doCreateBuild(branch, nd("${buildNo}", ''))
            doValidateBuild(
                    build,
                    vs,
                    ValidationRunStatusID.STATUS_PASSED
            )
        }
    }

    @Test
    void 'Getting all validation runs'() {
        def data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns {
                        pageInfo {
                            hasNextPage
                            hasPreviousPage
                            startCursor
                            endCursor
                        }
                        edges {
                            node {
                                build {
                                    name
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasPreviousPage == false
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasNextPage == false
        assert data.branches.first().validationStamps.first().validationRuns.edges.size() == 100
    }

    @Test
    void 'Getting 20 first runs'() {
        def data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns(first: 20) {
                        pageInfo {
                            hasNextPage
                            hasPreviousPage
                            startCursor
                            endCursor
                        }
                        edges {
                            node {
                                build {
                                    name
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasPreviousPage == false
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasNextPage == true
        assert data.branches.first().validationStamps.first().validationRuns.edges.size() == 20
    }

    @Test
    void 'Getting 20 first runs and then the next page'() {
        def data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns(first: 20) {
                        pageInfo {
                            hasNextPage
                            hasPreviousPage
                            startCursor
                            endCursor
                        }
                        edges {
                            node {
                                build {
                                    name
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasPreviousPage == false
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasNextPage == true
        assert data.branches.first().validationStamps.first().validationRuns.edges.size() == 20
        assert data.branches.first().validationStamps.first().validationRuns.edges.get(0).node.build.name == "100"
        def endCursor = data.branches.first().validationStamps.first().validationRuns.pageInfo.endCursor

        // Gets the next page
        data = run("""{
            branches (id: ${branch.id}) {
                validationStamps(name: "VS") {
                    validationRuns(first: 20, after: "${endCursor}") {
                        pageInfo {
                            hasNextPage
                            hasPreviousPage
                            startCursor
                            endCursor
                        }
                        edges {
                            node {
                                build {
                                    name
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasPreviousPage == true
        assert data.branches.first().validationStamps.first().validationRuns.pageInfo.hasNextPage == true
        assert data.branches.first().validationStamps.first().validationRuns.edges.size() == 20
        assert data.branches.first().validationStamps.first().validationRuns.edges.get(0).node.build.name == "80"

    }

}
