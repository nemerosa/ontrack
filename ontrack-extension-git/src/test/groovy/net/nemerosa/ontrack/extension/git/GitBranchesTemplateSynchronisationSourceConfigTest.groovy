package net.nemerosa.ontrack.extension.git

import org.junit.Test

import static net.nemerosa.ontrack.extension.git.GitBranchesTemplateSynchronisationSourceConfig.parse

class GitBranchesTemplateSynchronisationSourceConfigTest {

    @Test
    void 'Parsing - null'() {
        assert parse(null) == [] as Set
    }

    @Test
    void 'Parsing - empty'() {
        assert parse('') == [] as Set
    }

    @Test
    void 'Parsing - *'() {
        assert parse('*') == ['*'] as Set
    }

    @Test
    void 'Parsing - several lines'() {
        assert parse("""\
master
feature/*""") == ['master', 'feature/*'] as Set
    }

    @Test
    void 'Parsing - several lines and comments'() {
        assert parse("""\
master
# All features
feature/*""") == ['master', 'feature/*'] as Set
    }

}