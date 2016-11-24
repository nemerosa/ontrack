package net.nemerosa.ontrack.boot.graphql

import org.junit.Test

class BranchQLIT extends AbstractQLITSupport {

    @Test
    void 'Branch links'() {
        def branch = doCreateBranch()

        def data = run("""{branches (id: ${branch.id}) { name links { _page } } }""")
        assert data.branches.first().name == branch.name
        assert data.branches.first().links._page == "urn:test:#:entity:BRANCH:${branch.id}"
    }

}
