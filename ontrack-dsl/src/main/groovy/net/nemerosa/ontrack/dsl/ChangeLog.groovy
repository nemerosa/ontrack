package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL("Change log between two builds. See <<dsl-build-getChangeLog,`getChangeLog`>> method.")
class ChangeLog extends AbstractResource {

    ChangeLog(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("UUID of the change log.")
    String getUuid() {
        node['uuid']
    }

    @DSLMethod("Lower boundary of the change log.")
    Build getFrom() {
        new Build(
                ontrack,
                node.scmBuildFrom.buildView.build
        )
    }

    @DSLMethod("Upper boundary of the change log.")
    Build getTo() {
        new Build(
                ontrack,
                node.scmBuildTo.buildView.build
        )
    }

    @DSLMethod("List of commits in the change log.")
    List<ChangeLogCommit> getCommits() {
        // The commit link is available as _commits or as _revisions
        String url = optionalLink('commits')
        if (!url) {
            url = link('revisions')
        }
        ontrack.get(url)['commits'].collect {
            new ChangeLogCommit(ontrack, it)
        }
    }

    @DSLMethod("List of issues in the change log.")
    List<ChangeLogIssue> getIssues() {
        String url = optionalLink('issues')
        if (url) {
            return ontrack.get(url).list.collect {
                new ChangeLogIssue(ontrack, it['issue'])
            }
        } else {
            return []
        }
    }

    @DSLMethod("List of issues IDs in the change log.")
    List<String> getIssuesIds() {
        String url = optionalLink('issuesIds')
        if (url) {
            return ontrack.get(url) as List<String>
        } else {
            return []
        }
    }

    String exportIssues(IssueChangeLogExportRequest request = new IssueChangeLogExportRequest()) {
        return ontrack.text(
                query(
                        link('exportIssues'),
                        request.toQuery(from.id, to.id)
                )
        )
    }

    @DSLMethod("Export the issue change log. See <<dsl-usecases-changelogs-export,this section>> for an example.")
    String exportIssues(Map map) {
        return exportIssues(new IssueChangeLogExportRequest(map))
    }

    @DSLMethod("List of file changes in the change log.")
    List<ChangeLogFile> getFiles() {
        String url = optionalLink('files')
        if (url) {
            return ontrack.get(url).list.collect {
                new ChangeLogFile(ontrack, it)
            }
        } else {
            return []
        }
    }

}
