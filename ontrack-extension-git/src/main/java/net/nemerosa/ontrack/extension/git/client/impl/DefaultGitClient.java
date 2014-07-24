package net.nemerosa.ontrack.extension.git.client.impl;

import com.google.common.collect.Lists;
import net.nemerosa.ontrack.extension.git.client.*;
import net.nemerosa.ontrack.extension.git.client.plot.GPlot;
import net.nemerosa.ontrack.extension.git.client.plot.GitPlotRenderer;
import net.nemerosa.ontrack.extension.git.model.GitCommitNotFoundException;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.model.support.Time;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DefaultGitClient implements GitClient {

    private final Logger logger = LoggerFactory.getLogger(GitClient.class);
    private final GitRepository repository;
    private final GitConfiguration configuration;

    private GitTag getGitTagFromRef(Ref ref) {
        RevCommit commit = repository.getCommitForTag(ref);
        String tagName = getTagNameFromRef(ref);
        return new GitTag(
                tagName,
                Time.from(commit.getCommitTime() * 1000L)
        );
    }

    public DefaultGitClient(GitRepository repository, GitConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    private String getTagNameFromRef(Ref ref) {
        return StringUtils.substringAfter(
                ref.getName(),
                Constants.R_TAGS
        );
    }

    @Override
    public Collection<GitTag> getTags() {
        try {
            List<GitTag> tags = new ArrayList<>(
                    Lists.transform(
                            repository.git().tagList().call(),
                            this::getGitTagFromRef)
            );
            Collections.sort(tags, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));
            return tags;
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    @Override
    public GitConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public GitLog log(String from, String to) {
        try {
            // Client
            Git git = repository.git();
            Repository gitRepository = git.getRepository();
            // Gets boundaries
            Ref oFrom = gitRepository.getTags().get(from);
            Ref oTo = gitRepository.getTags().get(to);

            // Corresponding commits
            RevCommit commitFrom = repository.getCommitForTag(oFrom);
            RevCommit commitTo = repository.getCommitForTag(oTo);

            // Ordering of commits
            if (commitFrom.getCommitTime() < commitTo.getCommitTime()) {
                RevCommit t = commitFrom;
                commitFrom = commitTo;
                commitTo = t;
            }

            // Log
            PlotWalk walk = new PlotWalk(gitRepository);
            walk.markStart(walk.lookupCommit(commitFrom.getId()));
            walk.markUninteresting(walk.lookupCommit(commitTo.getId()));
            PlotCommitList<PlotLane> commitList = new PlotCommitList<>();
            commitList.source(walk);
            commitList.fillTo(1000); // TODO How to set the maximum? See RevWalkUtils#count ?

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
            throw new GitIOException(e);
        }
    }

    @Override
    public GitDiff diff(String from, String to) {
        try {
            // Client
            Git git = repository.git();
            Repository gitRepository = git.getRepository();
            // Gets boundaries
            Ref oFrom = gitRepository.getTags().get(from);
            Ref oTo = gitRepository.getTags().get(to);

            // Corresponding commits
            RevCommit commitFrom = repository.getCommitForTag(oFrom);
            RevCommit commitTo = repository.getCommitForTag(oTo);

            // Ordering of commits
            if (commitFrom.getCommitTime() > commitTo.getCommitTime()) {
                RevCommit t = commitFrom;
                commitFrom = commitTo;
                commitTo = t;
            }

            // Diff command
            List<DiffEntry> entries = git.diff()
                    .setShowNameAndStatusOnly(true)
                    .setOldTree(getTreeIterator(commitFrom.getId()))
                    .setNewTree(getTreeIterator(commitTo.getId()))
                    .call();

            // OK
            return new GitDiff(
                    commitFrom,
                    commitTo,
                    Lists.transform(
                            entries,
                            diff -> new GitDiffEntry(
                                    toChangeType(diff.getChangeType()),
                                    diff.getOldPath(),
                                    diff.getNewPath()
                            )
                    ));

        } catch (IOException e) {
            throw new GitIOException(e);
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    @Override
    public boolean isCommitDefined(String commit) {
        try {
            return repository.git().getRepository().resolve(commit + "^0") != null;
        } catch (IOException e) {
            throw new GitIOException(e);
        }
    }

    @Override
    public GitCommit getCommitFor(String commit) {
        try {
            Repository repo = repository.git().getRepository();
            return toCommit(
                    new RevWalk(repo).parseCommit(
                            repo.resolve(commit + "^0")
                    )
            );
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Emulates the
     * <a href="https://www.kernel.org/pub/software/scm/git/docs/git-describe.html"><code>describe</code></a>
     * command.
     * <p>
     * For each committish supplied, git describe will first look for a tag which tags exactly that commit.
     * Annotated tags will always be preferred over lightweight tags, and tags with newer dates will always
     * be preferred over tags with older dates. If an exact match is found, its name will be output and
     * searching will stop.
     * <p>
     * If an exact match was not found, git describe will walk back through the commit history to locate an
     * ancestor commit which has been tagged. The ancestor’s tag will be output along with an abbreviation of
     * the input committish’s SHA1.
     * <p>
     * If multiple tags were found during the walk then the tag which has the fewest commits different from the
     * input committish will be selected and output. Here fewest commits different is defined as the number of
     * commits which would be shown by git log tag..input will be the smallest number of commits possible.
     */
    @Override
    public String getEarliestTagForCommit(String gitCommitId, Predicate<String> tagNamePredicate) {
        try {
            // Client
            Git git = repository.git();
            Repository gitRepository = git.getRepository();

            // 1 - look for exact match
            Map<String, String> commitTagIndex = new HashMap<>();
            List<Ref> tagRefs = git.tagList().call();
            for (Ref tagRef : tagRefs) {
                // Indexation
                String tagName = getTagNameFromRef(tagRef);
                if (tagNamePredicate.test(tagName)) {
                    // Gets the corresponding commit
                    logger.debug("[gitclient] Looking commit for tag {}", tagName);
                    RevCommit revCommit = repository.getCommitForTag(tagRef);
                    commitTagIndex.put(revCommit.getId().getName(), tagName);
                    // Equality?
                    if (revCommit.getId().getName().equals(gitCommitId)) {
                        return tagName;
                    }
                }
            }

            // 2 - walking back to the history

            // Gets boundaries
            ObjectId headObjectId = gitRepository.resolve("HEAD");
            ObjectId commitObjectId = gitRepository.resolve(gitCommitId);
            if (commitObjectId == null) {
                throw new GitCommitNotFoundException(gitCommitId);
            }
            // Walk
            RevWalk walk = new RevWalk(gitRepository);
            walk.markStart(walk.lookupCommit(headObjectId));
            walk.markUninteresting(walk.lookupCommit(commitObjectId));
            // List of commits between the commit and the HEAD
            String tag = null;
            for (RevCommit revCommit : walk) {
                // Gets the corresponding tag
                String candidateTagName = commitTagIndex.get(revCommit.getId().getName());
                if (candidateTagName != null && tagNamePredicate.test(candidateTagName)) {
                    tag = candidateTagName;
                }
            }
            // OK
            return tag;
        } catch (IOException ex) {
            throw new GitIOException(ex);
        } catch (GitAPIException e) {
            throw new GitException(e);
        }
    }

    @Override
    public boolean scanCommits(Predicate<RevCommit> scanFunction) {
        // Client
        Git git = repository.git();
        // All commits
        try {
            Iterable<RevCommit> commits = git.log().all().call();
            for (RevCommit commit : commits) {
                if (scanFunction.test(commit)) {
                    // Not going on
                    return true;
                }
            }
            // Default behaviour
            return false;
        } catch (GitAPIException e) {
            throw new GitException(e);
        } catch (IOException e) {
            throw new GitIOException(e);
        }
    }

    private GitChangeType toChangeType(DiffEntry.ChangeType changeType) {
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

    private AbstractTreeIterator getTreeIterator(ObjectId id)
            throws IOException {
        final CanonicalTreeParser p = new CanonicalTreeParser();
        Repository db = repository.git().getRepository();
        final ObjectReader or = db.newObjectReader();
        try {
            p.reset(or, new RevWalk(db).parseTree(id));
            return p;
        } finally {
            or.release();
        }
    }

    private String getId(RevCommit revCommit) {
        return revCommit.getId().getName();
    }

    @Override
    public GitCommit toCommit(RevCommit revCommit) {
        return new GitCommit(
                getId(revCommit),
                toPerson(revCommit.getAuthorIdent()),
                toPerson(revCommit.getCommitterIdent()),
                Time.from(1000L * revCommit.getCommitTime()),
                revCommit.getFullMessage(),
                revCommit.getShortMessage()
        );
    }

    @Override
    public void sync(Consumer<String> logger) {
        try {
            repository.sync(logger);
        } catch (GitAPIException e) {
            throw translationException(e);
        }
    }

    private GitPerson toPerson(PersonIdent ident) {
        return new GitPerson(
                ident.getName(),
                ident.getEmailAddress()
        );
    }

    protected GitException translationException(GitAPIException e) {
        throw new GitException(e);
    }
}
