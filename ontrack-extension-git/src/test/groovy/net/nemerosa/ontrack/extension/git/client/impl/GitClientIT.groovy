package net.nemerosa.ontrack.extension.git.client.impl

import net.nemerosa.ontrack.extension.git.client.GitClient
import org.junit.Test

class GitClientIT {

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
