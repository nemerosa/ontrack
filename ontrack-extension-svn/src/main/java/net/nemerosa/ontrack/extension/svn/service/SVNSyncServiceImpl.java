package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SVNSyncServiceImpl implements SVNSyncService {

    private final StructureService structureService;

    @Autowired
    public SVNSyncServiceImpl(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public SVNSyncInfoStatus launchSync(ID branchId) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.service.SVNSyncServiceImpl.launchSync
        return null;
    }

    @Override
    public SVNSyncInfoStatus getSyncStatus(String uuid) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.service.SVNSyncServiceImpl.getSyncStatus
        return null;
    }
}
