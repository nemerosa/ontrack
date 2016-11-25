package net.nemerosa.ontrack.boot.graphql

import graphql.GraphQLException
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test

class BranchQLIT extends AbstractQLITSupport {

    @Test
    void 'Branch links'() {
        def branch = doCreateBranch()

        def data = run("""{branches (id: ${branch.id}) { name links { _page } } }""")
        assert data.branches.first().name == branch.name
        assert data.branches.first().links._page == "urn:test:#:entity:BRANCH:${branch.id}"
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

}
