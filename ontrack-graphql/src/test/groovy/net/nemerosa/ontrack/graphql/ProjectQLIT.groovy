package net.nemerosa.ontrack.graphql

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
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
        def data = new GraphQL(ontrackSchema).execute('{project { id name }}').data
        assert data.project*.name.contains(p.name)
        assert data.project*.id.contains(p.id())
    }

    @Test
    void 'Project by ID'() {
        def p = doCreateProject()
        def data = new GraphQL(ontrackSchema).execute("{project(id: ${p.id}) { name }}").data
        assert data.project[0].name == p.name
    }

}
