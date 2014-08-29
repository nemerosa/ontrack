package net.nemerosa.ontrack.extension.git.client.impl

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

/**
 * Integration test for looking earliest tags for a commit in a Git repository.
 *
 * The integration test creates the following Git history for the tests:
 *
 * <pre>
 * * e7c13f8 (HEAD, master) Commit 10
 * * d7bd568 Commit 9
 * * a01269a (tag: 1.1.0) Commit 8
 * * 92cd6e2 Commit 7
 * * 7e8bea2 Commit 6
 * | * fca8d9a (tag: 1.0.1, 1.0) Commit 5
 * |/
 * * 95ccffb Commit 4
 * * ff4ff7c Commit 3
 * * 7e1f724 Commit 2
 * * 9c9bd64 Commit 1
 * </pre>
 *
 * (the commit SHA are for illustration purpose only, they cannot be relied upon)
 */
class GitClientEarliestTagIT {

    private static File repo

    /**
     * Execution of a command
     */
    private static void run(String cmd, String... args) {
        println "Running: $cmd ${args.join(' ')}"
        def list = [cmd]
        list.addAll args
        def process = new ProcessBuilder(list).directory(repo).start()
        def exit = process.waitFor()
        if (exit != 0) {
            println process.errorStream.text
            println process.inputStream.text
            assert false: "Command exited with error = $exit"
        } else {
            println process.inputStream.text
        }
    }

    /**
     * Preparation of the Git repository
     */
    @BeforeClass
    static void 'Git repository'() {
        // Gets a directory
        repo = File.createTempDir('ontrack-git', '')

        // Initialises a Git repository
        run('git', 'init')

        // Commits 1..4
        (1..4).each {
            commit(it)
        }

        // 1.0 branch and tag
        run('git', 'checkout', '-b', '1.0')
        commit(5)
        run('git', 'tag', '1.0.1')

        // Going further with the master
        run('git', 'checkout', 'master')

        // Commits and tags
        commit(6)
        commit(7)
        commit(8)
        run('git', 'tag', '1.1.0')
        commit(9)
        commit(10)

        // Log
        run('git', 'log', '--oneline', '--graph', '--decorate', 'master', '1.0')
    }

    protected static void commit(no) {
        def fileName = "file${no}"
        run('touch', fileName)
        run('git', 'add', fileName)
        run('git', 'commit', '-m', "Commit $no")
    }

    /**
     * Removing the Git repository
     */
    @AfterClass
    static void 'Git repository deletion'() {
        repo.deleteDir()
    }

    /**
     * When a tag is <i>on</i> the commit.
     *
     * <code>Commit 8</code> gives tag <code>1.1.0</code>.
     */
    @Test
    void 'tag_on_commit'() {

    }

    /**
     * When a tag is between the HEAD and the commit.
     *
     * <code>Commit 6</code> gives tag <code>1.1.0</code>.
     */
    @Test
    void 'tag_on_path_to_head'() {

    }

    /**
     * When a tag is <i>not</i> between the HEAD and the commit.
     *
     * <code>Commit 3</code> gives tag <code>1.0.1</code>.
     */
    @Test
    void 'tag_on_separate_path'() {

    }

}
