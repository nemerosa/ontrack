package net.nemerosa.ontrack.extension.scm.model

import org.junit.Test

class SCMFileChangeFiltersTest {

    @Test
    void 'Adding a filter to an empty list'() {
        SCMFileChangeFilters filters = SCMFileChangeFilters.create().save(new SCMFileChangeFilter(
                'SQL',
                ['**/*.sql']
        ))
        assert filters.filters.collect { it.name } == ['SQL']
    }

    @Test
    void 'Adding a new filter'() {
        SCMFileChangeFilters filters = SCMFileChangeFilters.create().save(new SCMFileChangeFilter(
                'SQL',
                ['**/*.sql']
        )).save(new SCMFileChangeFilter(
                'Build',
                ['**/*.gradle']
        ))
        assert filters.filters.collect { it.name } == ['Build', 'SQL']
    }

    @Test
    void 'Adding an existing filter'() {
        SCMFileChangeFilters filters = SCMFileChangeFilters.create().save(new SCMFileChangeFilter(
                'SQL',
                ['**/*.sql']
        )).save(new SCMFileChangeFilter(
                'SQL',
                ['**/*.sql', '**/*.ddl']
        ))
        assert filters.filters.size() == 1
        assert filters.filters[0].name == 'SQL'
        assert filters.filters[0].patterns == ['**/*.sql', '**/*.ddl']
    }

    @Test
    void 'Removing a filter from an empty list'() {
        SCMFileChangeFilters filters = SCMFileChangeFilters.create().remove('SQL')
        assert filters.filters.collect { it.name } == []
    }

    @Test
    void 'Removing a filter from a list'() {SCMFileChangeFilters filters = SCMFileChangeFilters.create().save(new SCMFileChangeFilter(
            'SQL',
            ['**/*.sql']
        )).save(new SCMFileChangeFilter(
            'Build',
            ['**/*.gradle']
        )).remove('SQL')
        assert filters.filters.collect { it.name } == ['Build']
    }

}
