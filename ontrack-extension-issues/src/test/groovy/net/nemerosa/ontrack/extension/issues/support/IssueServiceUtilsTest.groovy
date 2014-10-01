package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import org.junit.Test

import static org.mockito.Mockito.mock

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
    void 'Issue groups: no group among many'() {
        assert IssueServiceUtils.getIssueGroups(
                ['other'],
                [
                        'Bugs'    : ['bug'] as Set,
                        'Features': ['feature'] as Set,
                ]) == [] as Set
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

    @Test
    void 'Grouping issues: no issue'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        def groups = IssueServiceUtils.groupIssues(configuration, [], request, { config, i -> [] as Set })
        assert groups.isEmpty()
    }

    @Test
    void 'Grouping issues: no grouping'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        def issue = mock(Issue)
        def groups = IssueServiceUtils.groupIssues(configuration, [issue], request, { config, i -> ['bug'] as Set })
        assert groups.size() == 1
        assert groups[''] == [issue]
    }

    @Test
    void 'Grouping issues: grouping'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1 || i == bug2) {
                ['bug'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2, feature], request, issueTypeFn)
        assert groups.size() == 2
        assert groups['Bugs'] == [bug1, bug2]
        assert groups['Features'] == [feature]
    }

    @Test
    void 'Grouping issues: grouping and pruning'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1 || i == bug2) {
                ['bug'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2], request, issueTypeFn)
        assert groups.size() == 1
        assert groups['Bugs'] == [bug1, bug2]
    }

    @Test
    void 'Grouping issues: grouping with other (default)'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)
        def other = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1 || i == bug2) {
                ['bug'] as Set
            } else if (i == other) {
                ['other'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2, feature, other], request, issueTypeFn)
        assert groups.size() == 3
        assert groups['Bugs'] == [bug1, bug2]
        assert groups['Features'] == [feature]
        assert groups['Other'] == [other]
    }

    @Test
    void 'Grouping issues: grouping with other (custom)'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        request.altGroup = 'Unclassified'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)
        def other = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1 || i == bug2) {
                ['bug'] as Set
            } else if (i == other) {
                ['other'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2, feature, other], request, issueTypeFn)
        assert groups.size() == 3
        assert groups['Bugs'] == [bug1, bug2]
        assert groups['Features'] == [feature]
        assert groups['Unclassified'] == [other]
    }

    @Test(expected = IssueExportMoreThanOneGroupException)
    void 'Grouping issues: more than one group for an issue'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1 || i == bug2) {
                ['bug'] as Set
            } else {
                ['feature', 'bug'] as Set
            }
        }

        IssueServiceUtils.groupIssues(configuration, [bug1, bug2, feature], request, issueTypeFn)
    }

    @Test
    void 'Grouping issues: grouping with exclusion'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        request.exclude = 'delivery'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def feature = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1) {
                ['bug'] as Set
            } else if (i == bug2) {
                ['bug', 'delivery'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2, feature], request, issueTypeFn)
        assert groups.size() == 2
        assert groups['Bugs'] == [bug1]
        assert groups['Features'] == [feature]
    }

    @Test
    void 'Grouping issues: grouping with several exclusions'() {
        def configuration = mock(IssueServiceConfiguration)
        def request = new IssueChangeLogExportRequest()
        request.grouping = 'Bugs=bug|Features=feature'
        request.exclude = 'delivery, design'
        def bug1 = mock(Issue)
        def bug2 = mock(Issue)
        def bug3 = mock(Issue)
        def feature = mock(Issue)

        def issueTypeFn = { config, i ->
            if (i == bug1) {
                ['bug'] as Set
            } else if (i == bug2) {
                ['bug', 'delivery'] as Set
            } else if (i == bug3) {
                ['bug', 'design'] as Set
            } else {
                ['feature'] as Set
            }
        }

        def groups = IssueServiceUtils.groupIssues(configuration, [bug1, bug2, bug3, feature], request, issueTypeFn)
        assert groups.size() == 2
        assert groups['Bugs'] == [bug1]
        assert groups['Features'] == [feature]
    }

}