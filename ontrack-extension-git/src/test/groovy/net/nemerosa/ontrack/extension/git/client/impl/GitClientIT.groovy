package net.nemerosa.ontrack.extension.git.client.impl

import net.nemerosa.ontrack.extension.git.client.GitClient
import org.junit.Test

import java.util.stream.Collectors

class GitClientIT {

    @Test
    void 'Raw log: between HEAD and a commit ~ 1'() {
        def repo = new GitTestUtils()
        try {
            repo.with {
                run 'git', 'init'
                (1..6).each {
                    commit it
                }

                run('git', 'log', '--oneline', '--graph', '--decorate', '--all')
            }

            GitClient gitClient = repo.gitClient()

            def commit4 = repo.commitLookup('Commit 4')
            def log = gitClient.rawLog("${commit4}~1", 'HEAD').collect(Collectors.toList())
            assert log.collect { it.shortMessage } == ['Commit 6', 'Commit 5', 'Commit 4']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Log: between commits'() {
        def repo = new GitTestUtils()
        try {
            repo.with {
                run 'git', 'init'
                commit 1
                commit 2
                commit 3
                run 'git', 'tag', 'v3'
                commit 4
                run 'git', 'tag', 'v4'
                commit 5
                commit 6
                commit 7
                run 'git', 'tag', 'v7'
                commit 8

                run('git', 'log', '--oneline', '--graph', '--decorate', '--all')
            }

            GitClient gitClient = repo.gitClient()

            def commit4 = repo.commitLookup('Commit 4')
            def commit7 = repo.commitLookup('Commit 7')
            def log = gitClient.log(commit7, commit4)
            assert log.commits.collect { it.shortMessage } == ['Commit 7', 'Commit 6', 'Commit 5']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Log: between tags'() {
        def repo = new GitTestUtils()
        try {
            repo.with {
                run 'git', 'init'
                commit 1
                commit 2
                commit 3
                run 'git', 'tag', 'v3'
                commit 4
                run 'git', 'tag', 'v4'
                commit 5
                commit 6
                commit 7
                run 'git', 'tag', 'v7'
                commit 8

                run('git', 'log', '--oneline', '--graph', '--decorate', '--all')
            }

            GitClient gitClient = repo.gitClient()

            def log = gitClient.log('v7', 'v4')
            assert log.commits.collect { it.shortMessage } == ['Commit 7', 'Commit 6', 'Commit 5']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Collection of remote branches'() {
        def repo = new GitTestUtils()
        try {
            repo.with {

                // Initialises a Git repository
                run('git', 'init')

                // Commits 1..4, each one a branch
                (1..4).each {
                    commit(it)
                    run('git', 'branch', "feature/$it", 'HEAD')
                }

                // Log
                run('git', 'log', '--oneline', '--graph', '--decorate', '--all')
            }

            // Clones this repository
            GitTestUtils clone = new GitTestUtils()
            try {

                // Clone the repo
                clone.run('git', 'clone', repo.dir.absolutePath, '.')

                // List remotes for this list, using the command line
                clone.run('git', 'ls-remote', '--heads')

                // Creates a Git client for this repository
                GitClient gitClient = clone.gitClient()

                // Gets the list of branches
                def branches = gitClient.remoteBranches

                // Checks the list
                assert branches.sort() == (1..4).collect { "feature/$it" } + ["master"]

            } finally {
                clone.close()
            }

        } finally {
            repo.close()
        }
    }

}
