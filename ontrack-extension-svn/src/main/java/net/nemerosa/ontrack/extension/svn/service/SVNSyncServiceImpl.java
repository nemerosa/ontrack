package net.nemerosa.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.extension.svn.property.SVNSyncProperty;
import net.nemerosa.ontrack.extension.svn.property.SVNSyncPropertyType;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO Application info provider
@Service
public class SVNSyncServiceImpl implements SVNSyncService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    /**
     * Index of jobs per branches
     */
    private final Map<ID, SyncJob> jobs = new ConcurrentHashMap<>();

    /**
     * Runner for the synchronisation jobs
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("Synchronisation %s")
                    .build()
    );

    @Autowired
    public SVNSyncServiceImpl(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @Override
    public SVNSyncInfoStatus launchSync(ID branchId) {
        // Checks any current job
        if (jobs.containsKey(branchId)) {
            return SVNSyncInfoStatus.of(branchId).withMessage("A synchronisation is already running for this branch.");
        }
        // Loads the branch
        Branch branch = structureService.getBranch(branchId);
        // Checks the accesses
        securityService.checkProjectFunction(branch.projectId(), BuildCreate.class);
        // Gets the configuration property
        Property<SVNSyncProperty> syncProperty = propertyService.getProperty(branch, SVNSyncPropertyType.class);
        if (syncProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("The synchronisation has not been configured for this branch.");
        }
        // Creates a new job for this branch
        SyncJob job = new SyncJob(branch, syncProperty.getValue());
        // Submits the job
        executor.submit(job);
        // Indexes the job
        jobs.put(branchId, job);
        // Returns its status
        return job.getStatus();
    }

    @Override
    public SVNSyncInfoStatus getSyncStatus(ID branchId) {
        SyncJob job = jobs.get(branchId);
        if (job != null) {
            return job.getStatus();
        } else {
            return SVNSyncInfoStatus.of(branchId).finished();
        }
    }

    private class SyncJob implements Runnable {

        private final Branch branch;
        private final SVNSyncProperty syncProperty;

        public SyncJob(Branch branch, SVNSyncProperty syncProperty) {
            this.branch = branch;
            this.syncProperty = syncProperty;
        }

        @Override
        public void run() {
            // FIXME Method net.nemerosa.ontrack.extension.svn.service.SVNSyncServiceImpl.SyncJob.run

        }

        public SVNSyncInfoStatus getStatus() {
            return SVNSyncInfoStatus.of(branch.getId());
        }
    }
}
