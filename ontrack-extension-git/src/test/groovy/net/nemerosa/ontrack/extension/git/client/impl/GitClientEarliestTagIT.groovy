package net.nemerosa.ontrack.extension.git.client.impl

import net.nemerosa.ontrack.extension.git.client.GitClient
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Integration test for looking earliest tags for a commit in a Git repository.
 *
 * The integration test creates the following Git history for the tests:
 *
 * <pre>
 * * e7c13f8 (HEAD, master) Commit 13
 * * e7c13f8 Commit 12
 * * e7c13f8 Commit 11
 * * e7c13f8 (tag: 1.2.0) Commit 10
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
    private static String run(String cmd, String... args) {
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
            def output = process.inputStream.text
            println output
            return output
        }
    }

    /**
     * Preparation of the Git repository
     */
    @BeforeClass
    static void 'Git repository'() {
        // Gets a directory
        repo = File.createTempDir('ontrack-git', '')
        println "Git repo at $repo"

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
        run('git', 'tag', '1.2.0')
        commit(11)
        commit(12)
        commit(13)

        // Log
        run('git', 'log', '--oneline', '--graph', '--decorate', 'master', '1.0')
    }

    protected static void commit(no) {
        def fileName = "file${no}"
        run('touch', fileName)
        run('git', 'add', fileName)
        run('git', 'commit', '-m', "Commit $no")
    }

    protected static String commitLookup(String message) {
        def info = run('git', 'log', '-g', '--grep', message, '--pretty=format:%h')
        if (info) {
            info.trim()
        } else {
            throw new RuntimeException("Cannot find commit for message $message")
        }
    }

    /**
     * Removing the Git repository
     */
    @AfterClass
    static void 'Git repository deletion'() {
        repo.deleteDir()
    }

    private GitClient gitClient

    @Before
    void 'Git client'() {
        GitRepository gitRepository = new DefaultGitRepository(
                repo,
                "",
                "master",
                "id", userPasswordSupplier
        )
        GitConfiguration gitConfiguration = GitConfiguration.empty()
        gitClient = new DefaultGitClient(gitRepository, gitConfiguration)
    }

    /**
     * When a tag is <i>on</i> the commit.
     *
     * <code>Commit 8</code> gives tag <code>1.1.0</code>.
     */
    @Test
    void 'tag_on_commit'() {
        // Identifying SHA for "Commit 8"
        def commit = commitLookup('Commit 8')
        // Call
        def tag = gitClient.getEarliestTagForCommit(commit, { true })
        // Check
        assert tag == '1.1.0'
    }

    /**
     * When a tag is between the HEAD and the commit.
     *
     * <code>Commit 6</code> gives tag <code>1.1.0</code>.
     */
    @Test
    void 'tag_on_path_to_head'() {
        // Identifying SHA for "Commit 6"
        def commit = commitLookup('Commit 6')
        // Call
        def tag = gitClient.getEarliestTagForCommit(commit, { true })
        // Check
        assert tag == '1.1.0'
    }

    /**
     * When a tag is <i>not</i> between the HEAD and the commit.
     *
     * <code>Commit 3</code> gives tag <code>1.0.1</code>.
     */
    @Test
    void 'tag_on_separate_path'() {
        // Identifying SHA for "Commit 3"
        def commit = commitLookup('Commit 3')
        // Call
        def tag = gitClient.getEarliestTagForCommit(commit, { true })
        // Check
        assert tag == '1.0.1'
    }

    /**
     * When no tag is found.
     *
     * <code>Commit 11</code> gives no tag.
     */
    @Test
    void 'no_tag'() {
        // Identifying SHA for "Commit 11"
        def commit = commitLookup('Commit 11')
        // Call
        def tag = gitClient.getEarliestTagForCommit(commit, { true })
        // Check
        assert tag == null
    }

}
