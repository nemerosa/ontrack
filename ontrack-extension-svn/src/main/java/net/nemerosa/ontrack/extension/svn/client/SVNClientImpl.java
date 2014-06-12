package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.support.SVNLogEntryCollector;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.*;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SVNClientImpl implements SVNClient {

    private final TransactionService transactionService;

    @Autowired
    public SVNClientImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public boolean exists(SVNRepository repository, SVNURL url, SVNRevision revision) {
        // Tries to gets information
        try {
            SVNInfo info = getWCClient(repository).doInfo(url, revision, revision);
            return info != null;
        } catch (SVNException ex) {
            return false;
        }
    }

    @Override
    public long getRepositoryRevision(SVNRepository repository, SVNURL url) {
        try {
            SVNInfo info = getWCClient(repository).doInfo(url, SVNRevision.HEAD, SVNRevision.HEAD);
            return info.getCommittedRevision().getNumber();
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public void log(SVNRepository repository, SVNURL url, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision stopRevision, boolean stopOnCopy, boolean discoverChangedPaths, long limit, boolean includeMergedRevisions, ISVNLogEntryHandler isvnLogEntryHandler) {
        try {
            getLogClient(repository).doLog(url, null, pegRevision, startRevision, stopRevision, stopOnCopy, discoverChangedPaths,
                    includeMergedRevisions, limit, null, isvnLogEntryHandler);
        } catch (SVNException e) {
            throw translateSVNException(e);
        }
    }

    @Override
    public boolean isTrunkOrBranch(SVNRepository repository, String path) {
        return isTrunk(path) || isBranch(repository, path);
    }

    @Override
    public List<Long> getMergedRevisions(SVNRepository repository, SVNURL url, long revision) {
        // Checks that the URL exists at both R-1 and R
        SVNRevision rm1 = SVNRevision.create(revision - 1);
        SVNRevision r = SVNRevision.create(revision);
        boolean existRM1 = exists(repository, url, rm1);
        boolean existR = exists(repository, url, r);
        try {
            // Both revisions must be valid in order to get some merges in between
            if (existRM1 && existR) {
                // Gets the changes in merge information
                SVNDiffClient diffClient = getDiffClient(repository);
                @SuppressWarnings("unchecked")
                Map<SVNURL, SVNMergeRangeList> before = diffClient.doGetMergedMergeInfo(url, rm1);
                @SuppressWarnings("unchecked")
                Map<SVNURL, SVNMergeRangeList> after = diffClient.doGetMergedMergeInfo(url, r);
                // Gets the difference between the two merge informations
                Map<SVNURL, SVNMergeRangeList> change;
                if (after != null && before != null) {
                    change = new HashMap<>();
                    for (Map.Entry<SVNURL, SVNMergeRangeList> entry : after.entrySet()) {
                        SVNURL source = entry.getKey();
                        SVNMergeRangeList afterMergeRangeList = entry.getValue();
                        SVNMergeRangeList beforeMergeRangeList = before.get(source);
                        if (beforeMergeRangeList != null) {
                            SVNMergeRangeList changeRangeList = afterMergeRangeList.diff(beforeMergeRangeList, false);
                            if (!changeRangeList.isEmpty()) {
                                change.put(source, changeRangeList);
                            }
                        } else {
                            change.put(source, afterMergeRangeList);
                        }
                    }
                } else {
                    change = after;
                }
                if (change == null || change.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    SVNLogEntryCollector collector = new SVNLogEntryCollector();
                    for (Map.Entry<SVNURL, SVNMergeRangeList> entry : change.entrySet()) {
                        SVNURL source = entry.getKey();
                        SVNMergeRangeList mergeRangeList = entry.getValue();
                        SVNMergeRange[] mergeRanges = mergeRangeList.getRanges();
                        for (SVNMergeRange mergeRange : mergeRanges) {
                            SVNRevision endRevision = SVNRevision.create(mergeRange.getEndRevision());
                            SVNRevision startRevision = SVNRevision.create(mergeRange.getStartRevision());
                            log(repository, source, endRevision, startRevision, endRevision, true, false, 0, false, collector);
                        }
                    }
                    List<Long> revisions = new ArrayList<>();
                    for (SVNLogEntry entry : collector.getEntries()) {
                        revisions.add(entry.getRevision());
                    }
                    return revisions;
                }
            } else {
                // One of the revisions (R-1 or R) is missing
                return Collections.emptyList();
            }
        } catch (SVNException ex) {
            throw translateSVNException(ex);
        }
    }

    private boolean isBranch(SVNRepository repository, String path) {
        return isPathOK(repository.getBranchPattern(), path);
    }

    private boolean isPathOK(String pattern, String path) {
        return org.apache.commons.lang.StringUtils.isNotBlank(pattern) && Pattern.matches(pattern, path);
    }

    private boolean isTrunk(String path) {
        return isPathOK(".+/trunk", path);
    }

    private SVNClientException translateSVNException(SVNException e) {
        return new SVNClientException(e);
    }

    protected SVNWCClient getWCClient(SVNRepository repository) {
        return getClientManager(repository).getWCClient();
    }

    protected SVNLogClient getLogClient(SVNRepository repository) {
        return getClientManager(repository).getLogClient();
    }

    protected SVNDiffClient getDiffClient(SVNRepository repository) {
        return getClientManager(repository).getDiffClient();
    }

    protected SVNClientManager getClientManager(final SVNRepository repository) {
        // Gets the current transaction
        Transaction transaction = transactionService.get();
        if (transaction == null) {
            throw new IllegalStateException("All SVN calls must be part of a SVN transaction");
        }
        // Gets the client manager
        return transaction
                .getResource(
                        SVNSession.class,
                        repository.getId(),
                        () -> {
                            // Creates the client manager for SVN
                            SVNClientManager clientManager = SVNClientManager.newInstance();
                            // Authentication (if needed)
                            String svnUser = repository.getConfiguration().getUser();
                            String svnPassword = repository.getConfiguration().getPassword();
                            if (StringUtils.isNotBlank(svnUser) && StringUtils.isNotBlank(svnPassword)) {
                                clientManager.setAuthenticationManager(new BasicAuthenticationManager(svnUser, svnPassword));
                            }
                            // OK
                            return new SVNSessionImpl(clientManager);
                        }
                )
                .getClientManager();
    }
}
