package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test

class BranchBuildsFilterQLIT extends AbstractQLITSupport {

    @Test
    void 'Default filter with validation stamp'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, NameDescription.nd('1', ''))
        doValidateBuild(build, 'VS', ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == [build.name]

        data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "NONE"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == []
    }

    @Test
    void 'Default filter with validation stamp and returning validation runs'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, NameDescription.nd('1', ''))
        doValidateBuild(build, 'VS', ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS"}) {
                    edges {
                        node {
                            name
                            validationRuns {
                                validationRunStatuses {
                                    statusID {
                                        id
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == [build.name]
        assert data.branches.first().builds.edges.node.validationRuns.validationRunStatuses.statusID.id.flatten() == ['PASSED']
    }

    @Test
    void 'Default filter with validation stamp status'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, NameDescription.nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('1', '')), vs, ValidationRunStatusID.STATUS_FAILED)
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS", withValidationStampStatus: "PASSED"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['2']
    }

}
