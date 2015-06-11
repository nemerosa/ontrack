package net.nemerosa.ontrack.dsl

class ChangeLogCommits extends AbstractResource {

    ChangeLogCommits(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    List<ChangeLogCommit> getCommits() {
        return node['commits'].collect {
            new ChangeLogCommit(ontrack, it)
        }
    }

}
