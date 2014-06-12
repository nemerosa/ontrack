package net.nemerosa.ontrack.extension.svn.client;


import net.nemerosa.ontrack.tx.TransactionResource;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public interface SVNSession extends TransactionResource {

    SVNClientManager getClientManager();

}
