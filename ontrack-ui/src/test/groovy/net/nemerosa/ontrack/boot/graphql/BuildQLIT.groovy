package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.security.BuildCreate
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.Test

import java.time.LocalDateTime

class BuildQLIT extends AbstractQLITSupport {

    @Test
    void 'Build creation'() {
        def branch = doCreateBranch()
        def build = asUser().with(branch, BuildCreate).call {
            structureService.newBuild(
                    Build.of(
                            branch,
                            NameDescription.nd('1', ''),
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
        assert p.value == '{"value":"value 1"}'
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
        assert p.value == '{"value":"value 2"}'
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
        assert p.value == '{"value":"value 2"}'
        assert p.editable == false
    }

}
