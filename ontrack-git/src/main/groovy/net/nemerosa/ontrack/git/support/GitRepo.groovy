package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient

class GitRepo {

    private final File dir

    GitRepo() {
        this(File.createTempDir('ontrack-git', '') as File)
    }

    GitRepo(File dir) {
        this.dir = dir
    }

    /**
     * Preparing a repository
     */
    def static prepare(Closure preparation) {
        def origin = new GitRepo()
        preparation.delegate = origin
        preparation(origin)
        [
                withClone: { clientAction ->
                    try {
                        File wd = File.createTempDir('ontrack-git', '')
                        try {
                            // Client
                            def client = cloneRepo(wd, origin)
                            // Utility test access
                            def clientRepo = new GitRepo(wd)
                            // Runs the action
                            clientAction(client, clientRepo, origin)
                        } finally {
                            wd.deleteDir()
                        }
                    } finally {
                        origin.close()
                    }
                },
                and      : { clientAction ->
                    clientAction(origin.client, origin)
                }
        ]
    }

    /**
     * Cloning a local test repository
     */
    static GitRepositoryClient cloneRepo(File wd, GitRepo origin) {
        // Repository definition for the `origin` repository
        GitRepository originRepository = new GitRepository(
                'file',
                'test',
                origin.dir.absolutePath,
                '', ''
        )
        // Creates the client
        new GitRepositoryClientImpl(
                wd,
                originRepository
        )
    }

    @Override
    String toString() {
        return dir.toString()
    }

    File getDir() {
        return dir
    }

    void close() {
        dir.deleteDir()
    }

    String git(String... args) {
        cmd('git', args)
    }

    String cmd(String executable, String... args) {
        def output = Utils.run(dir, executable, args)
        println output
        return output
    }

    /**
     * Creates or updates a file with some content, and optionally adds it to the index
     */
    void file(String path, String content, boolean add = true) {
        def file = new File(dir, path)
        file.parentFile.mkdirs()
        file.text = content
        if (add) {
            git 'add', path
        }
    }

    /**
     * Deletes a file
     */
    void delete(String path) {
        def file = new File(dir, path)
        if (file.exists()) {
            git 'rm', path
        }
    }

    void commit(def no, def message = '') {
        String fileName = "file${no}"
        cmd 'touch', fileName
        git 'add', fileName
        def commitMessage = message ?: "Commit $no"
        git 'commit', '-m', commitMessage
    }

    String commitLookup(String message, boolean shortId = true) {
        def commitFormat = shortId ? '%h' : '%H'
        def info = git 'log', '--all', '--grep', message, "--pretty=format:${commitFormat}"
        if (info) {
            info.trim()
        } else {
            throw new RuntimeException("Cannot find commit for message $message")
        }
    }

    GitRepositoryClient getClient() {
        new GitRepositoryClientImpl(
                dir,
                new GitRepository("test", "test", "", "", "")
        )
    }

}
