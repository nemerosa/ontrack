package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.tx.Transaction;
import net.nemerosa.ontrack.tx.TransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

@Component
public class SVNClientImpl implements SVNClient {

    private final TransactionService transactionService;

    @Autowired
    public SVNClientImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
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

    private SVNClientException translateSVNException(SVNException e) {
        return new SVNClientException(e);
    }

    protected SVNWCClient getWCClient(SVNRepository repository) {
        return getClientManager(repository).getWCClient();
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
