package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.exceptions.*
import net.nemerosa.ontrack.git.model.*
import net.nemerosa.ontrack.git.model.plot.GitPlotRenderer
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.filter.MessageRevFilter
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.String.format
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

class GitRepositoryClientImpl(
        private val repositoryDir: File,
        private val repository: GitRepository
) : GitRepositoryClient {

    private val logger = LoggerFactory.getLogger(GitRepositoryClient::class.java)
    private val git: Git
    private val credentialsProvider: CredentialsProvider?
    private val sync = ReentrantLock()

    private val isClonedOrCloning: Boolean
        get() = File(repositoryDir, ".git").exists()

    override val remoteBranches: List<String>
        get() {
            try {
                return git.lsRemote().setHeads(true).call()
                        .map { ref -> StringUtils.removeStart(ref.name, "refs/heads/") }
            } catch (e: GitAPIException) {
                throw GitRepositoryAPIException(repository.remote, e)
            }

        }

    override val synchronisationStatus: GitSynchronisationStatus
        get() = when {
            sync.isLocked -> GitSynchronisationStatus.RUNNING
            isClonedOrCloning -> GitSynchronisationStatus.IDLE
            else -> GitSynchronisationStatus.NONE
        }

    override

    val branches: GitBranchesInfo
        get() = if (!isClonedOrCloning) {
            GitBranchesInfo.empty()
        } else if (sync.tryLock()) {
            try {
                val repo = git.repository
                val revWalk = RevWalk(repo)
                val branchRefs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                val index = TreeMap<String, GitCommit>()
                for (ref in branchRefs) {
                    val branchName = StringUtils.removeStart(ref.name, "refs/remotes/origin/")
                    if (!StringUtils.equals("HEAD", branchName)) {
                        val revCommit = revWalk.parseCommit(ref.objectId)
                        val gitCommit = toCommit(revCommit)
                        index[branchName] = gitCommit
                    }
                }
                GitBranchesInfo(
                        index.entries
                                .map { entry -> GitBranchInfo(entry.key, entry.value) }
                )
            } catch (e: GitAPIException) {
                throw GitRepositoryAPIException(repository.remote, e)
            } catch (e: IOException) {
                throw GitRepositoryIOException(repository.remote, e)
            } finally {
                sync.unlock()
            }
        } else {
            GitBranchesInfo.empty()
        }

    override fun getBranchesForCommit(commit: String): List<String> {
        try {
            val list = git.branchList()
                    .setContains(commit)
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
            return list.map {
                StringUtils.removeStart(it.name, "refs/remotes/origin/")
            }.map {
                StringUtils.removeStart(it, "refs/heads/")
            }.filter {
                it != "HEAD"
            }.distinct().sorted()
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }
    }

    override val tags: Collection<GitTag>
        get() {
            try {
                val repo = git.repository
                val revWalk = RevWalk(repo)
                return repo.refDatabase.getRefs(Constants.R_TAGS).values
                        .map { ref -> getGitTagFromRef(revWalk, ref) }
            } catch (e: IOException) {
                throw GitRepositoryIOException(repository.remote, e)
            }
        }

    init {
        // Gets the Git repository
        val gitRepository: Repository
        try {
            gitRepository = FileRepositoryBuilder()
                    .setWorkTree(repositoryDir)
                    .findGitDir(repositoryDir)
                    .build()
        } catch (e: IOException) {
            throw GitRepositoryInitException(e)
        }

        // Gets the Git
        git = Git(gitRepository)
        // Credentials
        credentialsProvider = if (StringUtils.isNotBlank(repository.user)) {
            UsernamePasswordCredentialsProvider(repository.user, repository.password)
        } else {
            null
        }
    }

    /**
     * Tries to ls-remote the heads
     */
    override fun test() {
        logger.debug(format("[git] Listing the remote heads in %s", repository.remote))
        try {
            git.lsRemote()
                    .setRemote(repository.remote)
                    .setHeads(true)
                    .setCredentialsProvider(credentialsProvider)
                    .call()
        } catch (e: GitAPIException) {
            throw GitTestException(e.message)
        }

    }

    override fun sync(logger: Consumer<String>) {
        if (sync.tryLock()) {
            try {
                // Clone or update?
                if (isClonedOrCloning) {
                    // Fetch
                    fetch(logger)
                } else {
                    // Clone
                    cloneRemote(logger)
                }
            } finally {
                sync.unlock()
            }
        } else {
            logger.accept(format("[git] %s is already synchronising, trying later", repository.remote))
        }
    }

    @Synchronized
    private fun fetch(logger: Consumer<String>) {
        logger.accept(format("[git] Pulling %s", repository.remote))
        try {
            git.fetch()
                    .setCredentialsProvider(credentialsProvider)
                    .call()
        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        }

        logger.accept(format("[git] Pulling done for %s", repository.remote))
    }

    @Synchronized
    private fun cloneRemote(logger: Consumer<String>) {
        logger.accept(format("[git] Cloning %s", repository.remote))
        try {
            CloneCommand()
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(repositoryDir)
                    .setURI(repository.remote)
                    .call()
        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        }

        // Check
        if (!isClonedOrCloning) {
            throw GitRepositoryCannotCloneException(repository.remote)
        }
        // Done
        logger.accept(format("[git] Clone done for %s", repository.remote))
    }

    override fun isCompatible(repository: GitRepository): Boolean {
        return this.repository == repository
    }

    override fun <T> forEachCommitFrom(
            branch: String,
            commit: String,
            code: (RevCommit) -> T?
    ): T? {
        try {
            val gitRepository = git.repository
            val oCommit = gitRepository.resolve(commit)
            val oHead = gitRepository.resolve(branch)
            val i = git.log().addRange(oCommit, oHead).call().iterator()
            while (i.hasNext()) {
                val revCommit = i.next()
                val value = code(revCommit)
                if (value != null) {
                    return value
                }
            }
            // Nothing found
            return null
        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }
    }

    override fun log(from: String, to: String): Stream<GitCommit> {
        try {
            val gitRepository = git.repository
            val oFrom = gitRepository.resolve(from)
            val oTo = gitRepository.resolve(to)
            return if (oFrom == null || oTo == null) {
                emptyList<GitCommit>().stream()
            } else {
                git.log()
                        .addRange(oFrom, oTo)
                        .call()
                        .map { this.toCommit(it) }
                        .stream()
            }

        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun graph(from: String, to: String): GitLog {
        try {
            val range = range(from, to, false)
            val walk = PlotWalk(git.repository)

            // Log
            walk.markStart(walk.lookupCommit(range.from.id))
            walk.markUninteresting(walk.lookupCommit(range.to.id))
            val commitList = PlotCommitList<PlotLane>()
            commitList.source(walk)
            commitList.fillTo(Integer.MAX_VALUE)

            // Rendering
            val renderer = GitPlotRenderer(commitList)
            val plot = renderer.plot

            // Gets the commits
            val commits = renderer.commits.map { this.toCommit(it) }

            // OK
            return GitLog(
                    plot,
                    commits
            )

        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun isPatternFound(token: String): Boolean {
        try {
            val log = git.log()
                    .all()
                    .setRevFilter(MessageRevFilter.create(token))
                    .setMaxCount(1)
            val commits = log.call()
            return commits.iterator().hasNext()
        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun findCommitForRegex(branch: String, regex: String): RevCommit? {
        try {
            val resolvedBranch = git.repository.resolve(getBranchRef(branch))
            return if (resolvedBranch != null) {
                val log = git.log()
                        .add(resolvedBranch)
                        .setRevFilter(MessageRevFilter.create("($regex)"))
                        .setMaxCount(1)
                val commits = log.call()
                val i = commits.iterator()
                if (i.hasNext()) {
                    i.next()
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    /**
     * {@inheritDoc}
     *
     *
     * **Note**: the JGit library does not support the `git-describe` command yet, hence
     * the use of the command line.
     *
     * @see net.nemerosa.ontrack.git.support.GitClientSupport.tagContains
     */
    override fun getTagsWhichContainCommit(gitCommitId: String): Collection<String> {
        return GitClientSupport.tagContains(repositoryDir, gitCommitId)
    }

    override fun unifiedDiff(from: String, to: String, pathFilter: Predicate<String>): String {
        try {
            val range = range(from, to)

            // Diff command
            var entries = git.diff()
                    .setShowNameAndStatusOnly(true)
                    .setOldTree(getTreeIterator(range.from.id))
                    .setNewTree(getTreeIterator(range.to.id))
                    .call()

            // Filtering the entries
            entries = entries.filter { entry -> pathFilter.test(entry.oldPath) || pathFilter.test(entry.newPath) }

            // Output
            val output = ByteArrayOutputStream()

            // Formatting
            val formatter = DiffFormatter(output)
            formatter.setRepository(git.repository)
            entries.forEach { entry -> formatDiffEntry(formatter, entry) }

            // OK
            return Utils.toString(output.toByteArray())

        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun download(branch: String, path: String): String? {
        // Sync first
        sync(Consumer { logger.debug(it) })
        // Git show
        return GitClientSupport.showPath(repositoryDir, getBranchRef(branch), path)
    }

    override fun reset() {
        try {
            FileUtils.forceDelete(repositoryDir)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    private fun formatDiffEntry(formatter: DiffFormatter, entry: DiffEntry) {
        try {
            formatter.format(entry)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun diff(from: String, to: String): GitDiff {
        try {
            val range = range(from, to)

            // Diff command
            val entries = git.diff()
                    .setShowNameAndStatusOnly(true)
                    .setOldTree(getTreeIterator(range.from.id))
                    .setNewTree(getTreeIterator(range.to.id))
                    .call()

            // OK
            return GitDiff(
                    range.from,
                    range.to,
                    entries.map { diff ->
                        GitDiffEntry(
                                toChangeType(diff.changeType),
                                diff.oldPath,
                                diff.newPath
                        )
                    }
            )

        } catch (e: GitAPIException) {
            throw GitRepositoryAPIException(repository.remote, e)
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun getCommitFor(id: String): GitCommit? {
        return try {
            val repo = git.repository
            val objectId = repo.resolve("$id^0")
            if (objectId != null) {
                toCommit(RevWalk(repo).parseCommit(objectId))
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }

    }

    private fun getGitTagFromRef(revWalk: RevWalk, ref: Ref): GitTag {
        val tagName = StringUtils.substringAfter(
                ref.name,
                Constants.R_TAGS
        )
        try {
            val revCommit = revWalk.parseCommit(ref.objectId)
            val commitTime = revCommit.commitTime
            val tagTime = Time.from(commitTime * 1000L)
            return GitTag(
                    tagName,
                    tagTime
            )
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun isCommit(commitish: String): Boolean {
        try {
            val repo = git.repository
            return repo.resolve(commitish) != null
        } catch (e: IOException) {
            throw GitRepositoryIOException(repository.remote, e)
        }

    }

    override fun getBranchRef(branch: String): String {
        return String.format("origin/%s", branch)
    }

    override fun getId(revCommit: RevCommit): String {
        return revCommit.id.name
    }

    override fun getShortId(revCommit: RevCommit): String {
        return try {
            git.repository.newObjectReader().abbreviate(revCommit.id).name()
        } catch (e: IOException) {
            revCommit.id.name
        }

    }

    override fun toCommit(revCommit: RevCommit): GitCommit {
        return GitCommit(
                getId(revCommit),
                getShortId(revCommit),
                toPerson(revCommit.authorIdent),
                toPerson(revCommit.committerIdent),
                Time.from(1000L * revCommit.commitTime),
                revCommit.fullMessage,
                revCommit.shortMessage
        )
    }

    private fun toPerson(ident: PersonIdent): GitPerson {
        return GitPerson(
                ident.name,
                ident.emailAddress
        )
    }

    private fun toChangeType(changeType: DiffEntry.ChangeType): GitChangeType {
        return when (changeType) {
            DiffEntry.ChangeType.ADD -> GitChangeType.ADD
            DiffEntry.ChangeType.COPY -> GitChangeType.COPY
            DiffEntry.ChangeType.DELETE -> GitChangeType.DELETE
            DiffEntry.ChangeType.MODIFY -> GitChangeType.MODIFY
            DiffEntry.ChangeType.RENAME -> GitChangeType.RENAME
            else -> throw IllegalArgumentException("Unknown diff change type: $changeType")
        }
    }

    private fun getTreeIterator(id: ObjectId): AbstractTreeIterator {
        val p = CanonicalTreeParser()
        val db = git.repository
        db.newObjectReader().use { or ->
            p.reset(or, RevWalk(db).parseTree(id))
            return p
        }
    }

    private fun range(from: String, to: String, reorder: Boolean = true): GitRange {
        val gitRepository = git.repository

        val oFrom = gitRepository.resolve(from)
        val oTo = gitRepository.resolve(to)

        val walk = RevWalk(gitRepository)

        var commitFrom = walk.parseCommit(oFrom)
        var commitTo = walk.parseCommit(oTo)

        if (reorder && commitFrom.commitTime > commitTo.commitTime) {
            val t = commitFrom
            commitFrom = commitTo
            commitTo = t
        }

        return GitRange(commitFrom, commitTo)
    }
}
