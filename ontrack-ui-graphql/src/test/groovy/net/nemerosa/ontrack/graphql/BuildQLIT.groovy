package net.nemerosa.ontrack.graphql

import graphql.GraphQLException
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.Test

import java.time.LocalDateTime

import static net.nemerosa.ontrack.model.structure.NameDescription.nd
import static net.nemerosa.ontrack.test.TestUtils.uid

class BuildQLIT extends AbstractQLITSupport {

    @Test
    void 'Build creation'() {
        def branch = doCreateBranch()
        def build = asUser().with(branch, BuildCreate).call {
            structureService.newBuild(
                    Build.of(
                            branch,
                            nd('1', ''),
                            Signature.of(
                                    LocalDateTime.of(2016, 11, 25, 14, 43),
                                    "test"
                            )
                    )
            )
        }

        def data = run("""{builds (id: ${build.id}) { creation { user time } } }""")
        assert data.builds.first().creation.user == 'test'
        assert data.builds.first().creation.time == '2016-11-25T14:43:00'
    }

    @Test
    void 'Build property by name'() {
        def build = doCreateBuild()
        setProperty(build, TestSimplePropertyType, new TestSimpleProperty("value 1"))
        def data = run("""{
            builds(id: ${build.id}) {
                testSimpleProperty { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""")
        def p = data.builds.first().testSimpleProperty
        assert p.type.typeName == 'net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType'
        assert p.type.name == 'Simple value'
        assert p.type.description == 'Value.'
        assert p.value.value.asText() == 'value 1'
        assert p.editable == false
    }

    @Test
    void 'Build property by list'() {
        def build = doCreateBuild()
        setProperty(build, TestSimplePropertyType, new TestSimpleProperty("value 2"))
        def data = run("""{
            builds(id: ${build.id}) {
                properties { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""")

        def p = data.builds.first().properties.find { it.type.name == 'Simple value' }
        assert p.type.typeName == 'net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType'
        assert p.type.name == 'Simple value'
        assert p.type.description == 'Value.'
        assert p.value.value.asText() == 'value 2'
        assert p.editable == false

        p = data.builds.first().properties.find { it.type.name == 'Configuration value' }
        assert p.type.typeName == 'net.nemerosa.ontrack.extension.api.support.TestPropertyType'
        assert p.type.name == 'Configuration value'
        assert p.type.description == 'Value.'
        assert p.value == null
        assert p.editable == false
    }

    @Test
    void 'Build property filtered by type'() {
        def build = doCreateBuild()
        setProperty(build, TestSimplePropertyType, new TestSimpleProperty("value 2"))
        def data = run("""{
            builds(id: ${build.id}) {
                properties(type: "net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType") { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""")

        assert data.builds.first().properties.size() == 1

        def p = data.builds.first().properties.first()
        assert p.type.typeName == 'net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType'
        assert p.type.name == 'Simple value'
        assert p.type.description == 'Value.'
        assert p.value.value.asText() == 'value 2'
        assert p.editable == false
    }

    @Test
    void 'Build property filtered by value'() {
        def build = doCreateBuild()
        setProperty(build, TestSimplePropertyType, new TestSimpleProperty("value 2"))
        def data = run("""{
            builds(id: ${build.id}) {
                properties(hasValue: true) { 
                    type { 
                        typeName 
                        name
                        description
                    }
                    value
                    editable
                }
            }
        }""")

        assert data.builds.first().properties.size() == 1

        def p = data.builds.first().properties.first()
        assert p.type.typeName == 'net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType'
        assert p.type.name == 'Simple value'
        assert p.type.description == 'Value.'
        assert p.value.value.asText() == 'value 2'
        assert p.editable == false
    }

    @Test
    void 'Build links are empty by default'() {
        def build = doCreateBuild()

        def data = run("""{
            builds(id: ${build.id}) {
                linkedBuilds {
                    name
                }
            }
        }""")

        assert data.builds.first().linkedBuilds != null
        assert data.builds.first().linkedBuilds.empty
    }

    @Test
    void 'Build links'() {
        def build = doCreateBuild()
        def targetBuild = doCreateBuild()

        asAdmin().execute {
            structureService.addBuildLink build, targetBuild
        }

        def data = run("""{
            builds(id: ${build.id}) {
                linkedBuilds {
                    name
                    branch {
                        name
                        project {
                            name
                        }
                    }
                }
            }
        }""")

        def links = data.builds.first().linkedBuilds
        assert links != null
        assert links.size() == 1

        def link = links.first()
        assert link.name == targetBuild.name
        assert link.branch.name == targetBuild.branch.name
        assert link.branch.project.name == targetBuild.branch.project.name
    }

    @Test(expected = GraphQLException)
    void 'By branch not found'() {
        def project = doCreateProject()
        def branchName = uid('B')

        run("""{
            builds(project: "${project.name}", branch: "${branchName}") {
                name
            }
        }""")
    }

    @Test
    void 'By branch'() {
        def build = doCreateBuild()

        def data = run("""{
            builds(project: "${build.project.name}", branch: "${build.branch.name}") {
                id
            }
        }""")

        assert data.builds.first().id == build.id()
    }

    @Test(expected = GraphQLException)
    void 'By project not found'() {
        def projectName = uid('P')

        run("""{
            builds(project: "${projectName}") {
                name
            }
        }""")
    }

    @Test
    void 'By project'() {
        def build = doCreateBuild()

        def data = run("""{
            builds(project: "${build.project.name}") {
                id
            }
        }""")

        assert data.builds.first().id == build.id()
    }

    @Test
    void 'No argument means no result'() {
        doCreateBuild()

        def data = run("""{
            builds {
                id
            }
        }""")

        assert data.builds.empty
    }

    @Test
    void 'Branch filter'() {
        // Builds
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd('1', ''))
        doCreateBuild(branch, nd('2', ''))
        def pl = doCreatePromotionLevel(branch, nd('PL', ''))
        doPromote(build1, pl, '')
        // Query
        def data = run("""{
            builds(
                    project: "${branch.project.name}", 
                    branch: "${branch.name}", 
                    buildBranchFilter: {withPromotionLevel: "PL"}) {
                id
            }
        }""")
        // Results
        assert data.builds.size() == 1
        assert data.builds.get(0).id == build1.id()
    }

    @Test(expected = GraphQLException)
    void 'Branch filter requires a branch'() {
        // Builds
        def branch = doCreateBranch()
        def build1 = doCreateBuild(branch, nd('1', ''))
        doCreateBuild(branch, nd('2', ''))
        def pl = doCreatePromotionLevel(branch, nd('PL', ''))
        doPromote(build1, pl, '')
        // Query
        run("""{
            builds( 
                    buildBranchFilter: {withPromotionLevel: "PL"}) {
                id
            }
        }""")
    }

    @Test(expected = GraphQLException)
    void 'Project filter requires a project'() {
        // Builds
        doCreateBuild()
        // Query
        run("""{
            builds( 
                    buildProjectFilter: {promotionName: "PL"}) {
                id
            }
        }""")
    }

    @Test
    void 'Project filter'() {
        // Builds
        def project = doCreateProject()
        def branch1 = doCreateBranch(project, nd('1.0', ''))
        doCreateBuild(branch1, nd('1.0.0', ''))
        def branch2 = doCreateBranch(project, nd('2.0', ''))
        doCreateBuild(branch2, nd('2.0.0', ''))
        doCreateBuild(branch2, nd('2.0.1', ''))
        // Query
        def data = run("""{
            builds( 
                    project: "${project.name}",
                    buildProjectFilter: {branchName: "2.0"}) {
                name
            }
        }""")
        // Results
        assert data.builds.size() == 2
        assert data.builds.get(0).name == '2.0.1'
        assert data.builds.get(1).name == '2.0.0'
    }

}
