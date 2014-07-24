package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.common.BaseException;

public class GitNotSyncException extends BaseException {

    public GitNotSyncException() {
        super("Git repository not initialized. A call to the sync() method is probably missing.");
    }
}
