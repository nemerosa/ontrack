package net.nemerosa.ontrack.graphql

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class ProjectQLIT extends AbstractServiceTestSupport {

    @Autowired
    @Qualifier("ontrack")
    private GraphQLSchema ontrackSchema

    @Test
    void 'All projects'() {
        def p = doCreateProject()
        def data = new GraphQL(ontrackSchema).execute('{projects { id name }}').data
        assert data.projects*.name.contains(p.name)
        assert data.projects*.id.contains(p.id())
    }

    @Test
    void 'Project by ID'() {
        def p = doCreateProject()
        def data = new GraphQL(ontrackSchema).execute("{projects(id: ${p.id}) { name }}").data
        assert data.projects[0].name == p.name
    }

    @Test
    void 'Project branches'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("B1", ""))
        doCreateBranch(p, NameDescription.nd("B2", ""))
        def data = new GraphQL(ontrackSchema).execute("{projects(id: ${p.id}) { name branches { name } }}").data
        assert data.projects[0].branches*.name == ["B1", "B2"]
    }

}
