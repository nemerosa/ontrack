package net.nemerosa.ontrack.extension.svn.graphql

import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLITSupport
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static graphql.Scalars.GraphQLInt
import static graphql.Scalars.GraphQLLong
import static net.nemerosa.ontrack.test.TestUtils.uid

class SVNConfigurationGQLIT extends AbstractQLITSupport {

    @Autowired
    private SVNConfigurationService configurationService

    @Autowired
    private OntrackConfigProperties configProperties

    @Autowired
    private SVNConfigurationGQLType svnConfigurationGQLType

    @Before
    void cleaning() throws Exception {
        configProperties.configurationTest = false
        asAdmin().execute {
            configurationService.configurations.each {
                configurationService.deleteConfiguration(it.name)
            }
        }
    }

    @After
    void before() {
        configProperties.configurationTest = true
    }

    @Test
    void getting_svn_configurations() throws Exception {
        String n1 = uid("S")
        String n2 = uid("S")
        asAdmin().execute {
            configurationService.newConfiguration(SVNConfiguration.of(n1, "http://n1"))
            configurationService.newConfiguration(SVNConfiguration.of(n2, "http://n2"))

            def data = run("""{
                svnConfigurations {
                    name
                    url
                }
            }""")

            assert data.svnConfigurations.size() == 2
            assert data.svnConfigurations[0].name == n1
            assert data.svnConfigurations[1].name == n2
            assert data.svnConfigurations[0].url == "http://n1"
            assert data.svnConfigurations[1].url == "http://n2"
        }
    }

    @Test
    void getting_svn_configurations_with_filter() throws Exception {
        String n1 = uid("S")
        String n2 = uid("S")
        asAdmin().execute {
            configurationService.newConfiguration(SVNConfiguration.of(n1, "http://n1"))
            configurationService.newConfiguration(SVNConfiguration.of(n2, "http://n2"))

            def data = run("""{
                svnConfigurations(name: "${n1}") {
                    name
                    url
                }
            }""")

            assert data.svnConfigurations.size() == 1
            assert data.svnConfigurations[0].name == n1
            assert data.svnConfigurations[0].url == "http://n1"
        }
    }

    @Test
    void graphql_schema() {
        def type = svnConfigurationGQLType.type
        assert type.name == 'SVNConfiguration'
        assert type.fieldDefinitions*.name as Set == [
                'name',
                'url',
                'user',
                'tagFilterPattern',
                'browserForPath',
                'browserForRevision',
                'browserForChange',
                'indexationInterval',
                'indexationStart',
                'issueServiceConfigurationIdentifier',
        ] as Set
        // Checks the type of the 'indexationInterval'
        def definition = type.getFieldDefinition('indexationInterval')
        assert definition
        assert definition.name == 'indexationInterval'
        assert definition.type == GraphQLInt
        // Checks the type of the 'indexationStart'
        definition = type.getFieldDefinition('indexationStart')
        assert definition
        assert definition.name == 'indexationStart'
        assert definition.type == GraphQLLong
    }

}
