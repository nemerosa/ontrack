package net.nemerosa.ontrack.git.support;

import com.google.common.collect.Lists;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.common.Utils;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryAPIException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryCannotCloneException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryIOException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryInitException;
import net.nemerosa.ontrack.git.model.*;
import net.nemerosa.ontrack.git.model.plot.GPlot;
import net.nemerosa.ontrack.git.model.plot.GitPlotRenderer;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class GitRepositoryClientImpl implements GitRepositoryClient {

    private final File repositoryDir;
    private final GitRepository repository;
    private final Git git;
    private final CredentialsProvider credentialsProvider;
    private final ReentrantLock sync = new ReentrantLock();

    public GitRepositoryClientImpl(File repositoryDir, GitRepository repository) {
        this.repositoryDir = repositoryDir;
        this.repository = repository;
        // Gets the Git repository
        Repository gitRepository;
        try {
            gitRepository = new FileRepositoryBuilder()
                    .setWorkTree(repositoryDir)
                    .findGitDir(repositoryDir)
                    .build();
        } catch (IOException e) {
            throw new GitRepositoryInitException(e);
        }
        // Gets the Git
        git = new Git(gitRepository);
        // Credentials
        if (StringUtils.isNotBlank(repository.getUser())) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(repository.getUser(), repository.getPassword());
        } else {
            credentialsProvider = null;
        }
    }

    @Override
    public void sync(Consumer<String> logger) {
        if (sync.tryLock()) {
            try {
                // Clone or update?
                if (new File(repositoryDir, ".git").exists()) {
                    // Fetch
                    fetch(logger);
                } else {
                    // Clone
                    cloneRemote(logger);
                }
            } finally {
                sync.unlock();
            }
        } else {
            logger.accept(format("[git] %s is already synchronising, trying later", repositoryDir));
        }
    }

    protected synchronized void fetch(Consumer<String> logger) {
        logger.accept(format("[git] Pulling %s into %s", repository.getRemote(), repositoryDir));
        try {
            git.fetch()
                    .setCredentialsProvider(credentialsProvider)
                    .call();
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        }
        logger.accept(format("[git] Pulling done for %s", repository.getRemote()));
    }

    protected synchronized void cloneRemote(Consumer<String> logger) {
        logger.accept(format("[git] Cloning %s into %s", repository.getRemote(), repositoryDir));
        try {
            new CloneCommand()
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(repositoryDir)
                    .setURI(repository.getRemote())
                    .call();
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        }
        // Check
        if (!new File(repositoryDir, ".git").exists()) {
            throw new GitRepositoryCannotCloneException(repository.getRemote());
        }
        // Done
        logger.accept(format("[git] Clone done for %s", repository.getRemote()));
    }

    @Override
    public boolean isCompatible(GitRepository repository) {
        return Objects.equals(this.repository, repository);
    }

    @Override
    public Stream<GitCommit> log(String from, String to) {
        try {
            Repository gitRepository = git.getRepository();
            ObjectId oFrom = gitRepository.resolve(from);
            ObjectId oTo = gitRepository.resolve(to);
            if (oFrom == null || oTo == null) {
                return Collections.<GitCommit>emptyList().stream();
            } else {
                return Lists.newArrayList(
                        git.log()
                                .addRange(oFrom, oTo)
                                .call()
                ).stream().map(this::toCommit);
            }

        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public GitLog graph(String from, String to) {
        try {
            GitRange range = range(from, to, false);
            PlotWalk walk = new PlotWalk(git.getRepository());

            // Log
            walk.markStart(walk.lookupCommit(range.getFrom().getId()));
            walk.markUninteresting(walk.lookupCommit(range.getTo().getId()));
            PlotCommitList<PlotLane> commitList = new PlotCommitList<>();
            commitList.source(walk);
            commitList.fillTo(Integer.MAX_VALUE);

            // Rendering
            GitPlotRenderer renderer = new GitPlotRenderer(commitList);
            GPlot plot = renderer.getPlot();

            // Gets the commits
            List<GitCommit> commits = Lists.transform(
                    renderer.getCommits(),
                    this::toCommit
            );

            // OK
            return new GitLog(
                    plot,
                    commits
            );

        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public boolean scanCommits(String branch, Predicate<RevCommit> scanFunction) {
        // All commits
        try {
            ObjectId resolvedBranch = git.getRepository().resolve(getBranchRef(branch));
            if (resolvedBranch != null) {
                Iterable<RevCommit> commits = git.log().add(resolvedBranch).call();
                for (RevCommit commit : commits) {
                    if (scanFunction.test(commit)) {
                        // Not going on
                        return true;
                    }
                }
            }
            // Default behaviour
            return false;
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note</b>: the JGit library does not support the <code>git-describe</code> command yet, hence
     * the use of the command line.
     *
     * @see net.nemerosa.ontrack.git.support.GitClientSupport#tagContains(java.io.File, String)
     */
    @Override
    public Collection<String> getTagsWhichContainCommit(String gitCommitId) {
        return GitClientSupport.tagContains(repositoryDir, gitCommitId);
    }

    @Override
    public List<String> getRemoteBranches() {
        try {
            return git.lsRemote().setHeads(true).call().stream()
                    .map(ref -> StringUtils.removeStart(ref.getName(), "refs/heads/"))
                    .collect(Collectors.toList());
        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        }
    }

    @Override
    public String unifiedDiff(String from, String to, Predicate<String> pathFilter) {
        try {
            GitRange range = range(from, to);

            // Diff command
            List<DiffEntry> entries = git.diff()
                    .setShowNameAndStatusOnly(true)
                    .setOldTree(getTreeIterator(range.getFrom().getId()))
                    .setNewTree(getTreeIterator(range.getTo().getId()))
                    .call();

            // Filtering the entries
            entries = entries.stream()
                    .filter(entry -> pathFilter.test(entry.getOldPath()) || pathFilter.test(entry.getNewPath()))
                    .collect(Collectors.toList());

            // Output
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            // Formatting
            DiffFormatter formatter = new DiffFormatter(output);
            formatter.setRepository(git.getRepository());
            entries.forEach(entry -> formatDiffEntry(formatter, entry));

            // OK
            return Utils.toString(output.toByteArray());

        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    private void formatDiffEntry(DiffFormatter formatter, DiffEntry entry) {
        try {
            formatter.format(entry);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public GitDiff diff(String from, String to) {
        try {
            GitRange range = range(from, to);

            // Diff command
            List<DiffEntry> entries = git.diff()
                    .setShowNameAndStatusOnly(true)
                    .setOldTree(getTreeIterator(range.getFrom().getId()))
                    .setNewTree(getTreeIterator(range.getTo().getId()))
                    .call();

            // OK
            return new GitDiff(
                    range.getFrom(),
                    range.getTo(),
                    Lists.transform(
                            entries,
                            diff -> new GitDiffEntry(
                                    toChangeType(diff.getChangeType()),
                                    diff.getOldPath(),
                                    diff.getNewPath()
                            )
                    ));

        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public Optional<GitCommit> getCommitFor(String id) {
        try {
            Repository repo = git.getRepository();
            ObjectId objectId = repo.resolve(id + "^0");
            if (objectId != null) {
                return Optional.of(toCommit(new RevWalk(repo).parseCommit(objectId)));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<GitTag> getTags() {
        try {
            Repository repo = git.getRepository();
            RevWalk revWalk = new RevWalk(repo);
            return repo.getRefDatabase().getRefs(Constants.R_TAGS).values().stream()
                    .map(ref -> {
                        String tagName = StringUtils.substringAfter(
                                ref.getName(),
                                Constants.R_TAGS
                        );
                        RevCommit revCommit = revWalk.lookupCommit(ref.getObjectId());
                        LocalDateTime tagTime = Time.from(revCommit.getCommitTime() * 1000L);
                        return new GitTag(
                                tagName,
                                tagTime
                        );
                    })
                    .collect(Collectors.toList())
                    ;
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public boolean isCommit(String commitish) {
        try {
            Repository repo = git.getRepository();
            return repo.resolve(commitish) != null;
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public String getBranchRef(String branch) {
        return String.format("origin/%s", branch);
    }

    @Override
    public String getId(RevCommit revCommit) {
        return revCommit.getId().getName();
    }

    @Override
    public String getShortId(RevCommit revCommit) {
        try {
            return git.getRepository().newObjectReader().abbreviate(revCommit.getId()).name();
        } catch (IOException e) {
            return revCommit.getId().getName();
        }
    }

    @Override
    public GitCommit toCommit(RevCommit revCommit) {
        return new GitCommit(
                getId(revCommit),
                getShortId(revCommit),
                toPerson(revCommit.getAuthorIdent()),
                toPerson(revCommit.getCommitterIdent()),
                Time.from(1000L * revCommit.getCommitTime()),
                revCommit.getFullMessage(),
                revCommit.getShortMessage()
        );
    }

    protected GitPerson toPerson(PersonIdent ident) {
        return new GitPerson(
                ident.getName(),
                ident.getEmailAddress()
        );
    }

    protected GitChangeType toChangeType(DiffEntry.ChangeType changeType) {
        switch (changeType) {
            case ADD:
                return GitChangeType.ADD;
            case COPY:
                return GitChangeType.COPY;
            case DELETE:
                return GitChangeType.DELETE;
            case MODIFY:
                return GitChangeType.MODIFY;
            case RENAME:
                return GitChangeType.RENAME;
            default:
                throw new IllegalArgumentException("Unknown diff change type: " + changeType);
        }
    }

    protected AbstractTreeIterator getTreeIterator(ObjectId id)
            throws IOException {
        final CanonicalTreeParser p = new CanonicalTreeParser();
        Repository db = git.getRepository();
        final ObjectReader or = db.newObjectReader();
        try {
            p.reset(or, new RevWalk(db).parseTree(id));
            return p;
        } finally {
            or.release();
        }
    }

    protected GitRange range(String from, String to) throws IOException {
        return range(from, to, true);
    }

    protected GitRange range(String from, String to, boolean reorder) throws IOException {
        Repository gitRepository = git.getRepository();

        ObjectId oFrom = gitRepository.resolve(from);
        ObjectId oTo = gitRepository.resolve(to);

        RevWalk walk = new RevWalk(gitRepository);

        RevCommit commitFrom = walk.parseCommit(oFrom);
        RevCommit commitTo = walk.parseCommit(oTo);

        if (reorder && commitFrom.getCommitTime() > commitTo.getCommitTime()) {
            RevCommit t = commitFrom;
            commitFrom = commitTo;
            commitTo = t;
        }

        return new GitRange(commitFrom, commitTo);
    }
}
