package net.nemerosa.ontrack.extension.svn.support

import org.apache.commons.io.FileUtils

import static net.nemerosa.ontrack.common.Utils.run

class SVNTestRepo {

    private final String repoName
    private File repo
    private long pid

    SVNTestRepo(String repoName) {
        this.repoName = repoName
    }

    static SVNTestRepo get(String repoName) {
        SVNTestRepo repo = new SVNTestRepo(repoName)
        repo.start()
        repo
    }

    long start() {
        repo = new File("build/repo/$repoName").absoluteFile
        FileUtils.deleteQuietly(repo)
        repo.mkdirs()
        println "SVN Test repo at ${repo.absolutePath}"
        // Creates the repository
        run repo, "svnadmin", "create", repo.absolutePath
        // Configuration file
        FileUtils.writeByteArrayToFile(new File(repo, 'conf/authz'), SVNTestUtils.class.getResourceAsStream('/svn/conf/authz').bytes)
        FileUtils.writeByteArrayToFile(new File(repo, 'conf/passwd'), SVNTestUtils.class.getResourceAsStream('/svn/conf/passwd').bytes)
        FileUtils.writeByteArrayToFile(new File(repo, 'conf/svnserve.conf'), SVNTestUtils.class.getResourceAsStream('/svn/conf/svnserve.conf').bytes)
        // Starts serving the repository
        def pidFile = new File(repo, 'pid')
        run repo, "svnserve", "--daemon", "--root", repo.absolutePath, "--pid-file", pidFile.absolutePath
        // Reads the PID file
        this.pid = pidFile.text as long
        println "SVN server started in $repo with pid=$pid"
        // OK
        pid
    }

    void stop() {
        if (pid != 0) {
            println "Stopping server with pid=$pid"
            run repo, "kill", "-KILL", "$pid"
        }
    }

    def mkdir(String path, String message) {
        run repo, 'svn', 'mkdir', '--message', message, '--parents', '--username', 'user', '--password', 'test', '--no-auth-cache', "svn://localhost/${path}"
    }

    /**
     * Merges {@code from} into {@code to} using the {@code wd} working directory.
     */
    static def merge(File wd, String from, String to, String message) {
        FileUtils.cleanDirectory wd
        // Checks the from out
        run wd, 'svn', 'checkout', "svn://localhost/${to}", wd.absolutePath
        // Merge the to
        run wd, 'svn', 'merge', '--accept', 'postpone', "svn://localhost/${from}", wd.absolutePath
        // Commit
        run wd, 'svn', 'commit', '--message', message, wd.absolutePath, '--username', 'user', '--password', 'test', '--no-auth-cache'
    }

    /**
     * Remote copy of {@code from} into {@code into} using the {@code message} message.
     */
    def copy(String from, String into, String message) {
        run repo, 'svn', 'copy', '--parents', "svn://localhost/${from}", "svn://localhost/${into}", '--message', message, '--username', 'user', '--password', 'test', '--no-auth-cache'
    }
}
