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
    void 'Branch by ID'() {
        def branch = doCreateBranch()

        def data = run("""{branches (id: ${branch.id}) { name } }""")
        assert data.branches.name == [branch.name]
    }

    @Test(expected = GraphQLException)
    void 'Branch by ID and project is not allowed'() {
        run("""{branches (id: 1, project: "test") { name } }""")
    }

    @Test
    void 'Branch by project'() {
        def project = doCreateProject()
        doCreateBranch(project, NameDescription.nd("B1", ""))
        doCreateBranch(project, NameDescription.nd("B2", ""))

        def data = run("""{branches (project: "${project.name}") { name } }""")
        assert data.branches.name == ['B1', 'B2']
    }

    @Test
    void 'Branch by project and name'() {
        def project = doCreateProject()
        doCreateBranch(project, NameDescription.nd("B1", ""))
        doCreateBranch(project, NameDescription.nd("B2", ""))
        doCreateBranch(project, NameDescription.nd("C1", ""))

        def data = run("""{branches (project: "${project.name}", name: "C.*") { name } }""")
        assert data.branches.name == ['C1']
    }

    @Test
    void 'Branch by name'() {
        def p1 = doCreateProject()
        def b1 = doCreateBranch(p1, NameDescription.nd("B1", ""))
        doCreateBranch(p1, NameDescription.nd("B2", ""))
        def p2 = doCreateProject()
        def b2 = doCreateBranch(p2, NameDescription.nd("B1", ""))

        def data = run("""{branches (name: "B1") { id } }""")
        assert data.branches.id == [b1.id(), b2.id()]
    }

    @Test
    void 'Branch signature'() {
        def branch = doCreateBranch()
        def data = run("""{branches (id: ${branch.id}) { creation { user time } } }""")
        assert data.branches.first().creation.user == 'user'
        assert data.branches.first().creation.time.charAt(10) == 'T'
    }

}
