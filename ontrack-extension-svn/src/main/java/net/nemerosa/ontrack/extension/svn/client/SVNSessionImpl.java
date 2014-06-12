package net.nemerosa.ontrack.extension.svn.client;

import org.tmatesoft.svn.core.wc.SVNClientManager;

public class SVNSessionImpl implements SVNSession {

    private final SVNClientManager clientManager;

    public SVNSessionImpl(SVNClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void close() {
        clientManager.dispose();
    }

    @Override
    public SVNClientManager getClientManager() {
        return clientManager;
    }

}
