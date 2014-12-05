package net.nemerosa.ontrack.git.support;

import com.google.common.collect.Lists;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryAPIException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryCannotCloneException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryIOException;
import net.nemerosa.ontrack.git.exceptions.GitRepositoryInitException;
import net.nemerosa.ontrack.git.model.GitCommit;
import net.nemerosa.ontrack.git.model.GitLog;
import net.nemerosa.ontrack.git.model.GitPerson;
import net.nemerosa.ontrack.git.model.GitRange;
import net.nemerosa.ontrack.git.model.plot.GPlot;
import net.nemerosa.ontrack.git.model.plot.GitPlotRenderer;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    public GitRepositoryClientImpl(File repositoryDir, GitRepository repository) {
        this.repositoryDir = repositoryDir;
        this.repository = repository;
        // Gets the Git repository
        Repository gitRepository;
        try {
            gitRepository = new FileRepositoryBuilder()
                    .setWorkTree(repositoryDir)
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
        // Clone or update?
        if (new File(repositoryDir, ".git").exists()) {
            // Fetch
            fetch(logger);
        } else {
            // Clone
            cloneRemote(logger);
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

            return Lists.newArrayList(
                    git.log()
                            .addRange(oFrom, oTo)
                            .call()
            ).stream().map(this::toCommit);

        } catch (GitAPIException e) {
            throw new GitRepositoryAPIException(repository.getRemote(), e);
        } catch (IOException e) {
            throw new GitRepositoryIOException(repository.getRemote(), e);
        }
    }

    @Override
    public GitLog graph(String from, String to) {
        try {
            GitRange range = range(from, to);
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
            Iterable<RevCommit> commits = git.log().add(git.getRepository().resolve(getBranchRef(branch))).call();
            for (RevCommit commit : commits) {
                if (scanFunction.test(commit)) {
                    // Not going on
                    return true;
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

    protected GitRange range(String from, String to) throws IOException {
        Repository gitRepository = git.getRepository();

        ObjectId oFrom = gitRepository.resolve(from);
        ObjectId oTo = gitRepository.resolve(to);

        RevWalk walk = new RevWalk(gitRepository);

        RevCommit commitFrom = walk.parseCommit(oFrom);
        RevCommit commitTo = walk.parseCommit(oTo);

        if (commitFrom.getCommitTime() < commitTo.getCommitTime()) {
            RevCommit t = commitFrom;
            commitFrom = commitTo;
            commitTo = t;
        }

        return new GitRange(commitFrom, commitTo);
    }
}
