package net.nemerosa.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class SVNSyncServiceImpl implements SVNSyncService, ApplicationInfoProvider {

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
        // Gets the project configurations for SVN
        Property<SVNProjectConfigurationProperty> projectConfigurationProperty =
                propertyService.getProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class);
        if (projectConfigurationProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("SVN has not been configured for this branch's project.");
        }
        // Gets the branch configurations for SVN
        Property<SVNBranchConfigurationProperty> branchConfigurationProperty =
                propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class);
        if (branchConfigurationProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("SVN has not been configured for this branch.");
        }
        // Creates a new job for this branch
        SyncJob job = new SyncJob(branch,
                projectConfigurationProperty.getValue(),
                branchConfigurationProperty.getValue(),
                syncProperty.getValue());
        // Submits the job
        executor.submit(job);
        // Indexes the job
        jobs.put(branchId, job);
        // Returns its status
        return job.getStatus();
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return jobs.values().stream()
                .map(SyncJob::getApplicationInfo)
                .collect(Collectors.toList());
    }

    private class SyncJob implements Runnable {

        private final Branch branch;
        private final SVNProjectConfigurationProperty projectConfigurationProperty;
        private final SVNBranchConfigurationProperty branchConfigurationProperty;
        private final SVNSyncProperty syncProperty;

        public SyncJob(Branch branch, SVNProjectConfigurationProperty projectConfigurationProperty, SVNBranchConfigurationProperty branchConfigurationProperty, SVNSyncProperty syncProperty) {
            this.branch = branch;
            this.projectConfigurationProperty = projectConfigurationProperty;
            this.branchConfigurationProperty = branchConfigurationProperty;
            this.syncProperty = syncProperty;
        }

        @Override
        public void run() {
            // TODO Gets the list of tags from SVN
            // TODO Qualifiers each tag to see if they match the build path pattern
            // TODO Creates the build
            // FIXME Method net.nemerosa.ontrack.extension.svn.service.SVNSyncServiceImpl.SyncJob.run

        }

        public SVNSyncInfoStatus getStatus() {
            return SVNSyncInfoStatus.of(branch.getId());
        }

        public ApplicationInfo getApplicationInfo() {
            return ApplicationInfo.info(
                    String.format(
                            "Running build synchronisation from SVN for branch %s/%s",
                            branch.getProject().getName(),
                            branch.getName()
                    )
            );
        }
    }
}
