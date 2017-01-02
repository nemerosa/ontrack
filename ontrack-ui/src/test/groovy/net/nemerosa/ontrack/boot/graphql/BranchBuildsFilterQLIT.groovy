package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.BuildConfig
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test

import java.time.LocalDateTime

import static net.nemerosa.ontrack.model.structure.NameDescription.nd

class BranchBuildsFilterQLIT extends AbstractQLITSupport {

    @Test
    void 'Default filter with validation stamp'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, nd('1', ''))
        doCreateValidationStamp(branch, nd('NONE', ''))
        doValidateBuild(build, 'VS', ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == [build.name]

        data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "NONE"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == []
    }

    @Test
    void 'Default filter with validation stamp and returning validation runs'() {
        def branch = doCreateBranch()
        def build = doCreateBuild(branch, nd('1', ''))
        doValidateBuild(build, 'VS', ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS"}) {
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
        }""")
        assert data.branches.first().builds.name.flatten() == [build.name]
        assert data.branches.first().builds.validationRuns.validationRunStatuses.statusID.id.flatten() == ['PASSED']
    }

    @Test
    void 'Default filter with validation stamp status'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, nd('1', '')), vs, ValidationRunStatusID.STATUS_FAILED)
        doValidateBuild(doCreateBuild(branch, nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withValidationStamp: "VS", withValidationStampStatus: "PASSED"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['2']
    }

    @Test
    void 'Default filter since validation stamp'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, nd('1', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doCreateBuild(branch, nd('3', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceValidationStamp: "VS"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['3', '2']
    }

    @Test
    void 'Default filter since validation stamp status'() {
        def branch = doCreateBranch()
        def vs = doCreateValidationStamp(branch, nd('VS', ''))
        doValidateBuild(doCreateBuild(branch, nd('1', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, nd('2', '')), vs, ValidationRunStatusID.STATUS_PASSED)
        doValidateBuild(doCreateBuild(branch, nd('3', '')), vs, ValidationRunStatusID.STATUS_FAILED)
        doCreateBuild(branch, nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceValidationStamp: "VS", sinceValidationStampStatus: "PASSED"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['4', '3', '2']
    }

    @Test
    void 'Default filter with promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, nd('COPPER', ''))
        doPromote(doCreateBuild(branch, nd('1', '')), copper, '')
        doCreateBuild(branch, nd('2', ''))
        doPromote(doCreateBuild(branch, nd('3', '')), copper, '')

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withPromotionLevel: "COPPER"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['3', '1']
    }

    @Test
    void 'Default filter since promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, nd('COPPER', ''))
        doPromote(doCreateBuild(branch, nd('1', '')), copper, '')
        doCreateBuild(branch, nd('2', ''))
        doPromote(doCreateBuild(branch, nd('3', '')), copper, '')
        doCreateBuild(branch, nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sincePromotionLevel: "COPPER"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['4', '3']
    }

    @Test
    void 'Default filter since and with promotion level'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, nd('COPPER', ''))
        def bronze = doCreatePromotionLevel(branch, nd('BRONZE', ''))

        doCreateBuild(branch, nd('1', ''))
        def build2 = doCreateBuild(branch, nd('2', ''))
        doPromote(build2, copper, '')
        doPromote(build2, bronze, '')
        doCreateBuild(branch, nd('3', ''))
        doPromote(doCreateBuild(branch, nd('4', '')), copper, '')
        doCreateBuild(branch, nd('5', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sincePromotionLevel: "BRONZE", withPromotionLevel: "COPPER"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['4', '2']
    }

    @Test
    void 'Default filter dates'() {
        def branch = doCreateBranch()
        doCreateBuild(branch, nd('1', ''), Signature.of(LocalDateTime.of(2016, 11, 30, 17, 00), 'test'))
        doCreateBuild(branch, nd('2', ''), Signature.of(LocalDateTime.of(2016, 12, 02, 17, 10), 'test'))
        doCreateBuild(branch, nd('3', ''), Signature.of(LocalDateTime.of(2016, 12, 04, 17, 20), 'test'))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {afterDate: "2016-12-01", beforeDate: "2016-12-03"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['2']
    }

    @Test
    void 'Default filter with property'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, nd('2', ''))
        doSetProperty(doCreateBuild(branch, nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['3', '1']
    }

    @Test
    void 'Default filter with property value'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, nd('2', ''))
        doSetProperty(doCreateBuild(branch, nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {withProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType", withPropertyValue: "1"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['1']
    }

    @Test
    void 'Default filter since property'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, nd('2', ''))
        doSetProperty(doCreateBuild(branch, nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))
        doCreateBuild(branch, nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {sinceProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType"}) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['4', '3']
    }

    @Test
    void 'Default filter since property value'() {
        def branch = doCreateBranch()
        doSetProperty(doCreateBuild(branch, nd('1', '')), TestSimplePropertyType, new TestSimpleProperty("1"))
        doCreateBuild(branch, nd('2', ''))
        doSetProperty(doCreateBuild(branch, nd('3', '')), TestSimplePropertyType, new TestSimpleProperty("3"))
        doCreateBuild(branch, nd('4', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(filter: {
                    sinceProperty: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType",
                    sincePropertyValue: "1"
                 }) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['4', '3', '2', '1']
    }

    @Test
    void 'Default filter with linked FROM criteria'() {
        // Project 1
        def branch1 = doCreateBranch()
        def build1 = doCreateBuild(branch1, nd('1.0', ''))
        // Project 2
        def branch2 = doCreateBranch()
        def build2 = doCreateBuild(branch2, nd('2.0', ''))
        // Link build 2 --> build 1
        asUser().with(build2, BuildConfig).with(build1, ProjectView).call {
            structureService.addBuildLink(
                    build2,
                    build1
            )
        }

        def data = asUser().withView(branch1).withView(branch2).call {
            run("""{
                branches (id: ${branch1.id}) {
                    builds(filter: {
                        linkedFrom: "${branch2.project.name}:*"
                     }) {
                        name
                    }
                }
            }""")
        }
        assert data.branches.first().builds.name.flatten() == ['1.0']
    }

    @Test
    void 'Default filter with linked TO criteria'() {
        // Project 1
        def branch1 = doCreateBranch()
        def build1 = doCreateBuild(branch1, nd('1.0', ''))
        // Project 2
        def branch2 = doCreateBranch()
        def build2 = doCreateBuild(branch2, nd('2.0', ''))
        // Link build 2 --> build 1
        asUser().with(build2, BuildConfig).with(build1, ProjectView).call {
            structureService.addBuildLink(
                    build2,
                    build1
            )
        }

        def data = asUser().withView(branch1).withView(branch2).call {
            run("""{
                branches (id: ${branch2.id}) {
                    builds(filter: {
                        linkedTo: "${branch1.project.name}:*"
                     }) {
                        name
                    }
                }
            }""")
        }
        assert data.branches.first().builds.name.flatten() == ['2.0']
    }

    @Test
    void 'Last promotion filter'() {
        def branch = doCreateBranch()
        def copper = doCreatePromotionLevel(branch, nd('COPPER', ''))
        def bronze = doCreatePromotionLevel(branch, nd('BRONZE', ''))
        def silver = doCreatePromotionLevel(branch, nd('SILVER', ''))

        doCreateBuild(branch, nd('1', ''))
        doPromote(doCreateBuild(branch, nd('2', '')), silver, '')
        doPromote(doCreateBuild(branch, nd('3', '')), bronze, '')
        doCreateBuild(branch, nd('4', ''))
        doPromote(doCreateBuild(branch, nd('5', '')), copper, '')
        doCreateBuild(branch, nd('6', ''))

        def data = run("""{
            branches (id: ${branch.id}) {
                builds(lastPromotions: true) {
                    name
                }
            }
        }""")
        assert data.branches.first().builds.name.flatten() == ['5', '3', '2']
    }

}
