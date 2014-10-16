package net.nemerosa.ontrack.extension.git.client.impl

import net.nemerosa.ontrack.common.Utils

class GitTestUtils {

    private final File dir = File.createTempDir('ontrack-git', '')

    @Override
    String toString() {
        return dir.toString()
    }

    File getDir() {
        return dir
    }

    public void close() {
        dir.deleteDir()
    }

    public String run(String cmd, String... args) {
        Utils.run(dir, cmd, args)
    }

    public void commit(def no) {
        def fileName = "file${no}"
        run('touch', fileName)
        run('git', 'add', fileName)
        run('git', 'commit', '-m', "Commit $no")
    }

    public String commitLookup(String message) {
        def info = run('git', 'log', '-g', '--grep', message, '--pretty=format:%h')
        if (info) {
            info.trim()
        } else {
            throw new RuntimeException("Cannot find commit for message $message")
        }
    }

}
