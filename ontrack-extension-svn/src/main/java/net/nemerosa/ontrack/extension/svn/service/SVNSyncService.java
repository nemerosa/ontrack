package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.model.structure.ID;

public interface SVNSyncService {

    SVNSyncInfoStatus launchSync(ID branchId);

}
