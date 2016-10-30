package net.nemerosa.ontrack.boot.graphql

import graphql.GraphQL
import graphql.schema.GraphQLSchema
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.Signature
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
    void 'Promotion levels for a branch'() {
        def branch = doCreateBranch()
        def project = branch.project
        (1..5).each {
            doCreatePromotionLevel(branch, NameDescription.nd("PL${it}", "Promotion level ${it}"))
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                    }
                }
            }
        }""")
        assert data.projects[0].branches.promotionLevels.name.flatten() == (1..5).collect { "PL${it}" }
    }

    @Test
    void 'Promotion runs for a promotion level'() {
        def pl = doCreatePromotionLevel()
        def branch = pl.branch
        def project = branch.project
        (1..5).each {
            def build = doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
            if (it % 2 == 0) {
                asUser().with(project, PromotionRunCreate).call {
                    structureService.newPromotionRun(
                            PromotionRun.of(
                                    build,
                                    pl,
                                    Signature.of('test'),
                                    "Promotion"
                            )
                    )
                }
            }
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                        promotionRuns {
                            edges {
                                node {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.promotionLevels.promotionRuns.edges.node.build.name.flatten() == ['4', '2']
    }

    @Test
    void 'Filtered list of promotion runs for a promotion level'() {
        def pl = doCreatePromotionLevel()
        def branch = pl.branch
        def project = branch.project
        (1..20).each {
            def build = doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
            if (it % 2 == 0) {
                asUser().with(project, PromotionRunCreate).call {
                    structureService.newPromotionRun(
                            PromotionRun.of(
                                    build,
                                    pl,
                                    Signature.of('test'),
                                    "Promotion"
                            )
                    )
                }
            }
        }
        def data = run("""{
            projects (id: ${project.id}) {
                branches (name: "${branch.name}") {
                    promotionLevels {
                        name
                        promotionRuns(first: 5) {
                            edges {
                                node {
                                    build {
                                        name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }""")
        assert data.projects.branches.promotionLevels.promotionRuns.edges.node.build.name.flatten() == ['20', '18', '16', '14', '12']
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

    @Test
    void 'Build edge query for a branch'() {
        def branch = doCreateBranch()
        def project = branch.project
        (1..20).each {
            doCreateBuild(branch, NameDescription.nd("${it}", "Build ${it}"))
        }
        def data = run("""{
            projects(id: ${project.id}) {
                name
                branches(name: "${branch.name}") {
                    name
                    builds(count: 5) {
                        edges {
                            node {
                                name
                            }
                        }
                    }
                }
            }
        }""")
        def builds = data.projects[0].branches[0].builds
        assert builds != null
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
