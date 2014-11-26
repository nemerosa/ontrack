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
        def repo = new GitTestUtils()
        try {
            repo.with {
                run 'git', 'init'
                commit 1
                commit 2
                run 'git', 'checkout', '-b', '2.1'
                commit 3
                run 'git', 'checkout', 'master'
                commit 4
                run 'git', 'checkout', '2.1'
                commit 5
                run 'git', 'tag', 'v2.1'
                run 'git', 'checkout', 'master'
                run 'git', 'merge', '--no-ff', '2.1', '--message', 'Merge 2.1' // M6
                commit 7
                run 'git', 'checkout', '-b', '2.2'
                commit 8
                commit 9
                run 'git', 'checkout', 'master'
                commit 10
                run 'git', 'checkout', '2.2'
                commit 11
                run 'git', 'tag', 'v2.2'

                run 'git', 'log', '--oneline', '--graph', '--decorate', '--all'
            }


            GitClient gitClient = repo.gitClient()

            def log = gitClient.log('v2.2', 'v2.1')
            assert log.commits.collect { it.shortMessage } == ['Commit 11', 'Commit 9', 'Commit 8', 'Commit 7', 'Merge 2.1', 'Commit 4']

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
        def repo = new GitTestUtils()
        try {
            repo.with {
                run 'git', 'init'
                commit 1
                commit 2
                run 'git', 'checkout', '-b', '2.1'
                commit 3
                run 'git', 'checkout', 'master'
                commit 4
                run 'git', 'checkout', '2.1'
                commit 5
                commit 6
                run 'git', 'checkout', 'master'
                commit 7
                commit 8
                run 'git', 'checkout', '2.1'
                commit 9
                run 'git', 'checkout', '-b', '2.2', 'master'
                commit 10
                commit 11
                run 'git', 'checkout', '2.1'
                commit 12
                run 'git', 'tag', 'v2.1'
                run 'git', 'checkout', '2.2'
                run 'git', 'merge', '--no-ff', '2.1', '--message', 'Merge 2.1->2.2'
                run 'git', 'checkout', 'master'
                commit 13
                run 'git', 'checkout', '2.2'
                commit 14
                run 'git', 'tag', 'v2.2'

                run 'git', 'log', '--oneline', '--graph', '--decorate', '--all'
            }


            GitClient gitClient = repo.gitClient()

            def log = gitClient.log('v2.2', 'v2.1')
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
