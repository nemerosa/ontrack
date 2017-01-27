package net.nemerosa.ontrack.graphql.support

import org.junit.Test

class GraphQLBeanConverterTest {

    @Test
    void 'Simple type'() {
        def type = GraphQLBeanConverter.asObjectType(Person)
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
        def type = GraphQLBeanConverter.asObjectType(Account)
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
        def type = GraphQLBeanConverter.asObjectType(OnBehalf)
        assert type.name == 'OnBehalf'
        def fields = type.fieldDefinitions.collectEntries { [it.name, it.type.name] }
        assert fields == [
                delegate: 'Account',
                account : 'Account',
        ]
    }

}
