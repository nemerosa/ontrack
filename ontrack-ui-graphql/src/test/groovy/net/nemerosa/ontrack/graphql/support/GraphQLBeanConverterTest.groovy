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

}
