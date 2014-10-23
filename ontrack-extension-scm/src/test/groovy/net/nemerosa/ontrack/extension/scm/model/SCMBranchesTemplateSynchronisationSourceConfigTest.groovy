package net.nemerosa.ontrack.extension.scm.model

import org.junit.Test

class SCMBranchesTemplateSynchronisationSourceConfigTest {

    @Test
    void 'Parsing - null'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.parse(null) == [] as Set
    }

    @Test
    void 'Parsing - empty'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.parse('') == [] as Set
    }

    @Test
    void 'Parsing - *'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.parse('*') == ['*'] as Set
    }

    @Test
    void 'Parsing - several lines'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.parse("""\
master
feature/*""") == ['master', 'feature/*'] as Set
    }

    @Test
    void 'Parsing - several lines and comments'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.parse("""\
master
# All features
feature/*""") == ['master', 'feature/*'] as Set
    }

    @Test
    void 'Matches - empty means all'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches([] as Set, 'any', true)
    }

    @Test
    void 'Matches - empty means none'() {
        assert !SCMBranchesTemplateSynchronisationSourceConfig.matches([] as Set, 'any', false)
    }

    @Test
    void 'Matches - *'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['*'] as Set, 'any', false)
    }

    @Test
    void 'Matches - exact'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['master'] as Set, 'master', false)
        assert !SCMBranchesTemplateSynchronisationSourceConfig.matches(['master'] as Set, 'master2', false)
    }

    @Test
    void 'Matches - pattern'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['master*'] as Set, 'master', false)
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['master*'] as Set, 'master2', false)
    }

    @Test
    void 'Matches - patterns'() {
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['master', 'feature/*'] as Set, 'master', false)
        assert SCMBranchesTemplateSynchronisationSourceConfig.matches(['master', 'feature/*'] as Set, 'feature/2', false)
        assert !SCMBranchesTemplateSynchronisationSourceConfig.matches(['master', 'feature/*'] as Set, 'fix/1', false)
        assert !SCMBranchesTemplateSynchronisationSourceConfig.matches(['master', 'feature/*'] as Set, 'feature', false)
    }

    @Test
    void 'Filter - no filter'() {
        def filter = new SCMBranchesTemplateSynchronisationSourceConfig(
                "",
                ""
        ).filter
        assert filter.test('feature/ontrack-111-project-manager')
        assert filter.test('feature/ontrack-40-templating')
        assert filter.test('fix/ontrack-110')
        assert filter.test('master')
    }

    @Test
    void 'Filter - includes all'() {
        def filter = new SCMBranchesTemplateSynchronisationSourceConfig(
                "*",
                ""
        ).filter
        assert filter.test('feature/ontrack-111-project-manager')
        assert filter.test('feature/ontrack-40-templating')
        assert filter.test('fix/ontrack-110')
        assert filter.test('master')
    }

    @Test
    void 'Filter - exclude master'() {
        def filter = new SCMBranchesTemplateSynchronisationSourceConfig(
                "",
                "master"
        ).filter
        assert filter.test('feature/ontrack-111-project-manager')
        assert filter.test('feature/ontrack-40-templating')
        assert filter.test('fix/ontrack-110')
        assert !filter.test('master')
    }

    @Test
    void 'Filter - include only'() {
        def filter = new SCMBranchesTemplateSynchronisationSourceConfig(
                "fix/*",
                ""
        ).filter
        assert !filter.test('feature/ontrack-111-project-manager')
        assert !filter.test('feature/ontrack-40-templating')
        assert filter.test('fix/ontrack-110')
        assert !filter.test('master')
    }

    @Test
    void 'Filter - include/exclude'() {
        def filter = new SCMBranchesTemplateSynchronisationSourceConfig(
                "feature/*",
                "*templating"
        ).filter
        assert filter.test('feature/ontrack-111-project-manager')
        assert !filter.test('feature/ontrack-40-templating')
        assert !filter.test('fix/ontrack-110')
        assert !filter.test('master')
    }

}