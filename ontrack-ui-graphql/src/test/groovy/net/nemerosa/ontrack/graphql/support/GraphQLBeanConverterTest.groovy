package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.junit.Before
import org.junit.Test

class GraphQLBeanConverterTest {

    private GQLTypeCache cache

    @Before
    void before() {
        cache = new GQLTypeCache()
    }

    @Test
    void 'Simple type'() {
        def type = GraphQLBeanConverter.asObjectType(Person, cache)
        assert type.name == 'Person'
        def fields = type.fieldDefinitions.collectEntries { [it.name, it.type.name] }
        assert fields == [
                name     : 'String',
                address  : 'String',
                age      : 'Int',
                developer: 'Boolean',
        ]
    }

    @Test
    void 'Composite type'() {
        def type = GraphQLBeanConverter.asObjectType(Account, cache)
        assert type.name == 'Account'
        def fields = type.fieldDefinitions.collectEntries { [it.name, it.type.name] }
        assert fields == [
                username: 'String',
                password: 'String',
                identity: 'Person',
        ]
    }

    @Test
    void 'Composite type with three levels'() {
        def type = GraphQLBeanConverter.asObjectType(OnBehalf, cache)
        assert type.name == 'OnBehalf'
        def fields = type.fieldDefinitions.collectEntries { [it.name, it.type.name] }
        assert fields == [
                delegate: 'Account',
                account : 'Account',
        ]
    }

}
