package net.nemerosa.ontrack.boot.graphql

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import static org.junit.Assert.fail

class ProjectQLIT extends AbstractServiceTestSupport {

    @Autowired
    @Qualifier("ontrack")
    private GraphQLSchema ontrackSchema

    @Test
    void 'All projects'() {
        def p = doCreateProject()
        def data = run('{projects { id name }}')
        assert data.projects*.name.contains(p.name)
        assert data.projects*.id.contains(p.id())
    }

    @Test
    void 'Project by ID'() {
        def p = doCreateProject()
        def data = run("{projects(id: ${p.id}) { name }}")
        assert data.projects[0].name == p.name
    }

    @Test
    void 'Project branches'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("B1", ""))
        doCreateBranch(p, NameDescription.nd("B2", ""))
        def data = run("{projects(id: ${p.id}) { name branches { name } }}")
        assert data.projects[0].branches*.name == ["B1", "B2"]
    }

    @Test
    void 'Branch by name'() {
        def p = doCreateProject()
        doCreateBranch(p, NameDescription.nd("B1", ""))
        doCreateBranch(p, NameDescription.nd("B2", ""))

        def query = """{projects(id: ${p.id}) { name branches(name: "B2") { name } } }"""
        println query
        def data = run(query)
        assert data.projects[0].branches*.name == ["B2"]
    }

    @Test
    void 'Builds for a branch'() {
        def b = doCreateBuild()
        def p = b.project
        def branchName = b.branch.name
        def data = run("""{
            projects(id: ${p.id}) {
                name
                branches(name: "${branchName}") {
                    name
                    builds {
                        edges {
                            node {
                                name
                            }
                        }
                    }
                }
            }
        }""")
        def rBranch = data.projects[0].branches[0]
        assert rBranch.builds.edges*.node.name == [b.name]
    }

    def run(String query) {
        def result = new GraphQL(ontrackSchema).execute(query)
        if (result.errors && !result.errors.empty) {
            fail result.errors*.message.join('\n')
        } else if (result.data) {
            return result.data
        } else {
            fail "No data was returned and no error was thrown."
        }
    }

}
