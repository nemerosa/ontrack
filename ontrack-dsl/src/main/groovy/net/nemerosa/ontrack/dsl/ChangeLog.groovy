package net.nemerosa.ontrack.dsl

class ChangeLog extends AbstractResource {

    ChangeLog(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getUuid() {
        node['uuid']
    }

    List<ChangeLogCommit> getCommits() {
        // The commit link is available as _commits or as _revisions
        String url = optionalLink('_commits')
        if (!url) {
            url = link('_revisions')
        }
        ontrack.get(url)['commits'].collect {
            new ChangeLogCommit(ontrack, it)
        }
    }

}
