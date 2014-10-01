package net.nemerosa.ontrack.extension.issues.support

import org.junit.Test

class IssueServiceUtilsTest {

    @Test
    void 'Issue groups: no types, no groups'() {
        assert IssueServiceUtils.getIssueGroups([], [:]).empty
    }

    @Test
    void 'Issue groups: no types, groups'() {
        assert IssueServiceUtils.getIssueGroups([], ['Bug': ['bug'] as Set]).empty
    }

    @Test
    void 'Issue groups: one group'() {
        assert IssueServiceUtils.getIssueGroups(['bug'], ['Bug': ['bug'] as Set]) == ['Bug'] as Set
    }

    @Test
    void 'Issue groups: one group among many'() {
        assert IssueServiceUtils.getIssueGroups(
                ['bug'],
                [
                        'Bugs'    : ['bug'] as Set,
                        'Features': ['feature'] as Set,
                ]) == ['Bugs'] as Set
    }

    @Test
    void 'Issue groups: one group among many, with several types'() {
        assert IssueServiceUtils.getIssueGroups(
                ['bug', 'gui'],
                [
                        'Bugs'    : ['bug'] as Set,
                        'Features': ['feature'] as Set,
                ]) == ['Bugs'] as Set
    }

    @Test
    void 'Issue groups: two groups'() {
        assert IssueServiceUtils.getIssueGroups(
                ['bug', 'feature'],
                [
                        'Bugs'    : ['bug'] as Set,
                        'Features': ['feature'] as Set,
                ]) == ['Bugs', 'Features'] as Set
    }

}