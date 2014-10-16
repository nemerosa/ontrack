package net.nemerosa.ontrack.extension.git

import org.junit.Test

import static net.nemerosa.ontrack.extension.git.GitBranchesTemplateSynchronisationSourceConfig.matches
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

    @Test
    void 'Matches - empty means all'() {
        assert matches([] as Set, 'any', true)
    }

    @Test
    void 'Matches - empty means none'() {
        assert !matches([] as Set, 'any', false)
    }

    @Test
    void 'Matches - *'() {
        assert matches(['*'] as Set, 'any', false)
    }

    @Test
    void 'Matches - exact'() {
        assert matches(['master'] as Set, 'master', false)
        assert !matches(['master'] as Set, 'master2', false)
    }

    @Test
    void 'Matches - pattern'() {
        assert matches(['master*'] as Set, 'master', false)
        assert matches(['master*'] as Set, 'master2', false)
    }

    @Test
    void 'Matches - patterns'() {
        assert matches(['master', 'feature/*'] as Set, 'master', false)
        assert matches(['master', 'feature/*'] as Set, 'feature/2', false)
        assert !matches(['master', 'feature/*'] as Set, 'fix/1', false)
        assert !matches(['master', 'feature/*'] as Set, 'feature', false)
    }

}