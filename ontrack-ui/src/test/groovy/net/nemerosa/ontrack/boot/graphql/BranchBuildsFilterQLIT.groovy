package net.nemerosa.ontrack.boot.graphql

import graphql.GraphQLException
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRun
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test

class BranchBuildsFilterQLIT extends AbstractQLITSupport {

    @Test
    void 'Default filter with validation stamp'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, NameDescription.nd('VS', ''))
        def build = doCreateBuild(branch, NameDescription.nd('1', ''))
        asUser().with(branch, ValidationRunCreate).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_PASSED,
                            ''
                    )
            )
        }

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "${vs.name}"}) {
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
        def vs = doCreateValidationStamp(branch, NameDescription.nd('VS', ''))
        def build = doCreateBuild(branch, NameDescription.nd('1', ''))
        asUser().with(branch, ValidationRunCreate).call {
            structureService.newValidationRun(
                    ValidationRun.of(
                            build,
                            vs,
                            1,
                            Signature.of('test'),
                            ValidationRunStatusID.STATUS_PASSED,
                            ''
                    )
            )
        }

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "${vs.name}"}) {
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

}
