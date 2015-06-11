package net.nemerosa.ontrack.dsl

class ChangeLog extends AbstractResource {

    ChangeLog(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getUuid() {
        node['uuid']
    }

    Build getFrom() {
        new Build(
                ontrack,
                node.scmBuildFrom.buildView.build
        )
    }

    Build getTo() {
        new Build(
                ontrack,
                node.scmBuildTo.buildView.build
        )
    }

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
