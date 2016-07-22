package net.nemerosa.ontrack.extension.svn.support

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.SVNURL
import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNCopySource
import org.tmatesoft.svn.core.wc.SVNRevision

class SVNTestRepo {

    public static withSvnRepo(String repoName, Closure closure) {
        SVNTestRepo repo = new SVNTestRepo(repoName)
        repo.start()
        try {
            closure(repo)
        } finally {
            repo.stop()
        }
    }

    private final String repoName
    private File repo
    private SVNURL url

    SVNTestRepo(String repoName) {
        this.repoName = repoName
    }

    static SVNTestRepo get(String repoName) {
        SVNTestRepo repo = new SVNTestRepo(repoName)
        repo.start()
        repo
    }

    void start() {
        repo = new File("build/repo/$repoName").absoluteFile
        FileUtils.deleteQuietly(repo)
        repo.mkdirs()
        println "SVN Test repo at ${repo.absolutePath}"
        // Creates the repository
        url = SVNRepositoryFactory.createLocalRepository(
                repo, null, true, false,
                false,
                false,
                false,
                false,
                true
        )
    }

    void stop() {
        FileUtils.forceDelete(repo)
    }

    SVNURL getUrl() {
        return url
    }

    protected static SVNClientManager getClientManager() {
        return SVNClientManager.newInstance();
    }

    def mkdir(String path, String message) {
        clientManager.commitClient.doMkDir(
                [url.appendPath(path, false)] as SVNURL[],
                message,
                null,
                true // Make parents
        )
    }

    static File createTempDir(String id) {
        File file = File.createTempFile("seed-test-${id}", '.d')
        FileUtils.forceDelete(file)
        file.mkdirs()
        file.deleteOnExit()
        return file
    }

    /**
     * Checks the code into a temporary directory and returns it
     */
    File checkout(String path = null) {
        def wc = createTempDir('svn')
        def downloadUrl = path ? url.appendPath(path, false) : url
        clientManager.updateClient.doCheckout(
                downloadUrl,
                wc,
                SVNRevision.HEAD,
                SVNRevision.HEAD,
                SVNDepth.INFINITY,
                false
        )
        wc
    }

    static def commit(File dir, String message) {
        clientManager.commitClient.doCommit(
                [dir] as File[],
                false,
                message,
                null,
                [] as String[],
                false,
                false,
                SVNDepth.INFINITY
        )
    }

    static def add(File dir, String path) {
        clientManager.getWCClient().doAdd(
                new File(dir, path),
                false,
                false,
                false,
                SVNDepth.INFINITY,
                false,
                true
        )
    }

    def file(String path, String content, String message) {
        File wc = checkout()
        try {
            // Edition
            File file = new File(wc, path)
            // Already there?
            boolean alreadyThere = file.exists()
            file.parentFile.mkdirs()
            file.text = content
            // Addition and commit
            if (!alreadyThere) {
                add(wc, path)
            }
            commit(wc, message)
        } finally {
            FileUtils.deleteQuietly(wc)
        }
    }

    /**
     * Merges {@code from} into {@code to} using a temporary working directory.
     */
    def merge(String from, String to, String message) {
        // Directory
        File wd = checkout(to)
        // Parsing (from)
        String fromPath
        SVNRevision fromRevision
        if (from.contains('@')) {
            fromPath = StringUtils.substringBefore(from, '@')
            fromRevision = SVNRevision.parse(StringUtils.substringAfter(from, '@'))
        } else {
            fromPath = from
            fromRevision = SVNRevision.HEAD
        }
        // Merging from into to
        clientManager.diffClient.doMerge(
                url.appendPath(fromPath, false), fromRevision,
                url.appendPath(fromPath, false), SVNRevision.HEAD,
                wd,
                SVNDepth.INFINITY,
                true,
                false,
                false,
                false
        )
        // Commit
        commit wd, message
    }

    /**
     * Remote copy of {@code from} into {@code into} using the {@code message} message.
     */
    long copy(String from, String into, String message) {
        // Parsing (from)
        String fromPath
        SVNRevision fromRevision
        if (from.contains('@')) {
            fromPath = StringUtils.substringBefore(from, '@')
            fromRevision = SVNRevision.parse(StringUtils.substringAfter(from, '@'))
        } else {
            fromPath = from
            fromRevision = SVNRevision.HEAD
        }
        // Copy
        clientManager.copyClient.doCopy(
                [new SVNCopySource(SVNRevision.HEAD, fromRevision, url.appendPath(fromPath, false))] as SVNCopySource[],
                url.appendPath(into, false),
                false, // move
                true,  // make parents
                true,  // fail when exists
                message,
                null
        ).newRevision
    }

}
