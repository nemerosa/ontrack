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
        def data = new GraphQL(ontrackSchema).execute('{project: { name }}').data
        println data
    }

}
