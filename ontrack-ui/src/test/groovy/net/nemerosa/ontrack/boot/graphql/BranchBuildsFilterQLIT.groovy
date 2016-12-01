package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test

import java.time.LocalDateTime

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

    @Test
    void 'Default filter since validation stamp'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, NameDescription.nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('1', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doCreateBuild(branch, NameDescription.nd('3', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceValidationStamp: "VS"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['3', '2']
    }

    @Test
    void 'Default filter since validation stamp status'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, NameDescription.nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('1', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, NameDescription.nd('3', '')), vs, ValidationRunStatusID.STATUS_FAILED)
        doCreateBuild(branch, NameDescription.nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceValidationStamp: "VS", sinceValidationStampStatus: "PASSED"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['4', '3', '2']
    }

    @Test
    void 'Default filter with promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, NameDescription.nd('COPPER', ''))
        doPromote(doCreateBuild(branch, NameDescription.nd('1', '')), copper, '')
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doPromote(doCreateBuild(branch, NameDescription.nd('3', '')), copper, '')

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withPromotionLevel: "COPPER"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['3', '1']
    }

    @Test
    void 'Default filter since promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, NameDescription.nd('COPPER', ''))
        doPromote(doCreateBuild(branch, NameDescription.nd('1', '')), copper, '')
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doPromote(doCreateBuild(branch, NameDescription.nd('3', '')), copper, '')
        doCreateBuild(branch, NameDescription.nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sincePromotionLevel: "COPPER"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['4', '3']
    }

    @Test
    void 'Default filter since and with promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, NameDescription.nd('COPPER', ''))
        def bronze = doCreatePromotionLevel(branch, NameDescription.nd('BRONZE', ''))

        doCreateBuild(branch, NameDescription.nd('1', ''))
        def build2 = doCreateBuild(branch, NameDescription.nd('2', ''))
        doPromote(build2, copper, '')
        doPromote(build2, bronze, '')
        doCreateBuild(branch, NameDescription.nd('3', ''))
        doPromote(doCreateBuild(branch, NameDescription.nd('4', '')), copper, '')
        doCreateBuild(branch, NameDescription.nd('5', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sincePromotionLevel: "BRONZE", withPromotionLevel: "COPPER"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['4', '2']
    }

    @Test
    void 'Default filter dates'() {
        def branch = doCreateBranch()
        doCreateBuild(branch, NameDescription.nd('1', ''), Signature.of(LocalDateTime.of(2016, 11, 30, 17, 00), 'test'))
        doCreateBuild(branch, NameDescription.nd('2', ''), Signature.of(LocalDateTime.of(2016, 12, 02, 17, 10), 'test'))
        doCreateBuild(branch, NameDescription.nd('3', ''), Signature.of(LocalDateTime.of(2016, 12, 04, 17, 20), 'test'))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {afterDate: "2016-12-01", beforeDate: "2016-12-03"}) {
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

    @Test
    void 'Default filter with property'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, NameDescription.nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doSetProperty(doCreateBuild(branch, NameDescription.nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['3', '1']
    }

    @Test
    void 'Default filter with property value'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, NameDescription.nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doSetProperty(doCreateBuild(branch, NameDescription.nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", withPropertyValue: "1"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['1']
    }

    @Test
    void 'Default filter since property'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, NameDescription.nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doSetProperty(doCreateBuild(branch, NameDescription.nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))
        doCreateBuild(branch, NameDescription.nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType"}) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['4', '3']
    }

    @Test
    void 'Default filter since property value'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, NameDescription.nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, NameDescription.nd('2', ''))
        doSetProperty(doCreateBuild(branch, NameDescription.nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))
        doCreateBuild(branch, NameDescription.nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {
                    sinceProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType",
                    sincePropertyValue: "1"
                 }) {
                    edges {
                        node {
                            name
                        }
                    }
                }
            }
        }""")
        assert data.branches.first().builds.edges.node.name.flatten() == ['4', '3', '2', '1']
    }

}
