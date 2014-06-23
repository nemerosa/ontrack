package net.nemerosa.ontrack.extension.jira.tx;

import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.tx.TransactionResource;

public interface JIRASession extends TransactionResource {

    JIRAClient getClient();

}
