package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitTestRepo
import org.junit.Test

import java.util.stream.Collectors

class GitRepositoryClientImplTest {

    @Test
    void 'Log: between HEAD and a commit ~ 1'() {
        def repo = new GitTestRepo()
        try {
            repo.with {
                git 'init'
                (1..6).each {
                    commit it
                }

                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            def commit4 = repo.commitLookup('Commit 4')
            def log = repo.client.log("${commit4}~1", 'HEAD').collect(Collectors.toList())
            assert log.collect { it.shortMessage } == ['Commit 6', 'Commit 5', 'Commit 4']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Graph: between commits'() {
        def repo = new GitTestRepo()
        try {
            repo.with {
                git 'init'
                commit 1
                commit 2
                commit 3
                git 'tag', 'v3'
                commit 4
                git 'tag', 'v4'
                commit 5
                commit 6
                commit 7
                git 'tag', 'v7'
                commit 8

                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            def commit4 = repo.commitLookup('Commit 4')
            def commit7 = repo.commitLookup('Commit 7')
            def log = repo.client.graph(commit7, commit4)
            assert log.commits.collect { it.shortMessage } == ['Commit 7', 'Commit 6', 'Commit 5']

        } finally {
            repo.close()
        }
    }

    /**
     * What is the change log for 2.2 since 2.1?
     * <pre>
     *     | * C11 (v2.2, 2.2)
     *     * | C10
     *     | * C9
     *     | * C8
     *     |/
     *     * C7
     *     * M6
     *     |\
     *     | * C5 (v2.1, 2.1)
     *     * | C4
     *     | * C3
     *     |/
     *     * C2
     *     * C1
     * </pre>
     *
     * We expect C4, M6, C7, C8, C10, C11
     */
    @Test
    void 'Log: between tags on different branches'() {
        def repo = new GitTestRepo()
        try {
            repo.with {
                git 'init'
                commit 1
                commit 2
                git 'checkout', '-b', '2.1'
                commit 3
                git 'checkout', 'master'
                commit 4
                git 'checkout', '2.1'
                commit 5
                git 'tag', 'v2.1'
                git 'checkout', 'master'
                git 'merge', '--no-ff', '2.1', '--message', 'Merge 2.1' // M6
                commit 7
                git 'checkout', '-b', '2.2'
                commit 8
                commit 9
                git 'checkout', 'master'
                commit 10
                git 'checkout', '2.2'
                commit 11
                git 'tag', 'v2.2'

                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            def log = repo.client.graph('v2.2', 'v2.1')
            assert log.commits.collect {
                it.shortMessage
            } == ['Commit 11', 'Commit 9', 'Commit 8', 'Commit 7', 'Merge 2.1', 'Commit 4']

        } finally {
            repo.close()
        }
    }

    /**
     * What is the change log for 2.2 since 2.1?
     * <pre>
     *     | * C14 (v2.2, 2.2)
     *     * | C13
     *     | * Merge 2.1->2.2
     *     | |\_________________
     *     | |                  \
     *     | |                  * C12 (v2.1, 2.1)
     *     | * C11              |
     *     | * C10              |
     *     |/                   |
     *     |                    * C9
     *     * C8                 |
     *     * C7                 |
     *     |                    |
     *     |                    * C6
     *     |                    * C5
     *     *                    | C4
     *     |                    * C3
     *     |--------------------/
     *     * C2
     *     * C1
     * </pre>
     *
     * We expect C4, C7, C8, C10, C11, Merge 2.1->2.2, C14
     */
    @Test
    void 'Log: between tags on different hierarchical branches'() {
        def repo = new GitTestRepo()
        try {
            repo.with {
                git 'init'
                commit 1
                commit 2
                git 'checkout', '-b', '2.1'
                commit 3
                git 'checkout', 'master'
                commit 4
                git 'checkout', '2.1'
                commit 5
                commit 6
                git 'checkout', 'master'
                commit 7
                commit 8
                git 'checkout', '2.1'
                commit 9
                git 'checkout', '-b', '2.2', 'master'
                commit 10
                commit 11
                git 'checkout', '2.1'
                commit 12
                git 'tag', 'v2.1'
                git 'checkout', '2.2'
                git 'merge', '--no-ff', '2.1', '--message', 'Merge 2.1->2.2'
                git 'checkout', 'master'
                commit 13
                git 'checkout', '2.2'
                commit 14
                git 'tag', 'v2.2'

                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            def log = repo.client.graph('v2.2', 'v2.1')
            assert log.commits.collect { it.shortMessage } == [
                    'Commit 14',
                    'Merge 2.1->2.2',
                    'Commit 11',
                    'Commit 10',
                    'Commit 8',
                    'Commit 7',
                    'Commit 4']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Log: between tags'() {
        def repo = new GitTestRepo()
        try {
            repo.with {
                git 'init'
                commit 1
                commit 2
                commit 3
                git 'tag', 'v3'
                commit 4
                git 'tag', 'v4'
                commit 5
                commit 6
                commit 7
                git 'tag', 'v7'
                commit 8

                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            def log = repo.client.graph('v7', 'v4')
            assert log.commits.collect { it.shortMessage } == ['Commit 7', 'Commit 6', 'Commit 5']

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Collection of remote branches'() {
        def repo = new GitTestRepo()
        try {
            repo.with {

                // Initialises a Git repository
                git 'init'

                // Commits 1..4, each one a branch
                (1..4).each {
                    commit it
                    git 'branch', "feature/$it", 'HEAD'
                }

                // Log
                git 'log', '--oneline', '--graph', '--decorate', '--all'
            }

            // Clones this repository
            GitTestRepo clone = new GitTestRepo()
            try {

                // Clone the repo
                clone.git 'clone', repo.dir.absolutePath, '.'

                // List remotes for this list, using the command line
                clone.git 'ls-remote', '--heads'

                // Gets the list of branches
                def branches = clone.client.remoteBranches

                // Checks the list
                assert branches.sort() == (1..4).collect { "feature/$it" } + ["master"]

            } finally {
                clone.close()
            }

        } finally {
            repo.close()
        }
    }

    @Test
    void 'Clone and fetch'() {
        def origin = new GitTestRepo()
        try {
            origin.with {
                // Initialises a Git repository
                git 'init'
                // Commits 1..4
                (1..4).each {
                    commit it
                }
            }

            // Repository definition for the `origin` repository
            GitRepository originRepository = new GitRepository(
                    'file',
                    'test',
                    origin.dir.absolutePath,
                    '', ''
            )

            // Gets a directory for the local working copy
            File wd = File.createTempDir('ontrack-git', '')
            try {

                // Creates the client
                GitRepositoryClient clone = new GitRepositoryClientImpl(
                        wd,
                        originRepository
                )

                // Test client
                GitTestRepo cloneRepo = new GitTestRepo(wd)

                // First sync (clone)
                clone.sync({ println it })

                // Gets the commits
                (1..4).each {
                    assert cloneRepo.commitLookup("Commit $it") != null
                }

                // Adds some commits on the origin repo
                origin.with {
                    (5..8).each {
                        commit it
                    }
                }

                // Second sync (fetch)
                clone.sync({ println it })

                // Gets the commits
                (5..8).each {
                    assert cloneRepo.commitLookup("Commit $it") != null
                }

            } finally {
                wd.deleteDir()
            }

        } finally {
            origin.close()
        }
    }

}