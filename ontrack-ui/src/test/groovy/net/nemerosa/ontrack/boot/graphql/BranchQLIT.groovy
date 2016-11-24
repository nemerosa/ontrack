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

}
