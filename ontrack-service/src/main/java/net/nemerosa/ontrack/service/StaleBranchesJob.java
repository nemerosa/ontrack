package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Detection and management of stale branches.
 */
@Component
public class StaleBranchesJob implements JobProvider {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final EventQueryService eventQueryService;

    @Autowired
    public StaleBranchesJob(StructureService structureService, PropertyService propertyService, EventQueryService eventQueryService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.eventQueryService = eventQueryService;
    }

    @Override
    public Collection<Job> getJobs() {
        return Collections.singletonList(
                new Job() {
                    @Override
                    public String getCategory() {
                        return "System";
                    }

                    @Override
                    public String getId() {
                        return "StaleBranches";
                    }

                    @Override
                    public String getDescription() {
                        return "Detection and management of stale branches";
                    }

                    @Override
                    public boolean isDisabled() {
                        return false;
                    }

                    /**
                     * Once a day
                     */
                    @Override
                    public int getInterval() {
                        return Job.DAY;
                    }

                    @Override
                    public JobTask createTask() {
                        return new RunnableJobTask(StaleBranchesJob.this::detectAndManageStaleBranches);
                    }
                }
        );
    }

    protected void detectAndManageStaleBranches(JobInfoListener infoListener) {
        // Disabling and deletion times
        // FIXME Gets the duration from the global settings
        int disablingDuration = 30;
        int deletionDuration = 0;
        // Nothing to do if no disabling time
        if (disablingDuration <= 0) {
            infoListener.post("No disabling time being set - exiting.");
        } else {
            // Current time
            LocalDateTime now = Time.now();
            // Disabling time
            LocalDateTime disablingTime = now.minusDays(disablingDuration);
            // Deletion time
            Optional<LocalDateTime> deletionTime =
                    Optional.ofNullable(
                            deletionDuration > 0 ?
                                    disablingTime.minusDays(deletionDuration) :
                                    null
                    );
            // Build filter to get the last build
            StandardBuildFilter filter = new StandardBuildFilter(
                    StandardBuildFilterData.of(1),
                    propertyService
            );
            // For all projects
            structureService.getProjectList().forEach(
                    project -> detectAndManageStaleBranches(infoListener, project, filter, disablingTime, deletionTime)
            );
        }
    }

    protected void detectAndManageStaleBranches(JobInfoListener infoListener, Project project, StandardBuildFilter filter, LocalDateTime disablingTime, Optional<LocalDateTime> deletionTime) {
        infoListener.post(format("[%s] Scanning project for stale branches", project.getName()));
        structureService.getBranchesForProject(project.getId()).forEach(
                branch -> detectAndManageStaleBranch(infoListener, branch, filter, disablingTime, deletionTime)
        );
    }

    protected void detectAndManageStaleBranch(JobInfoListener infoListener, Branch branch, StandardBuildFilter filter, LocalDateTime disablingTime, Optional<LocalDateTime> deletionTime) {
        infoListener.post(format("[%s][%s] Scanning branch for staleness", branch.getProject().getName(), branch.getName()));
        // Last date
        LocalDateTime lastTime;
        // Last build on this branch
        List<Build> builds = structureService.getFilteredBuilds(branch.getId(), filter);
        if (builds.isEmpty()) {
            infoListener.post(format("[%s][%s] No available build - taking branch's creation time", branch.getProject().getName(), branch.getName()));
            // Takes the branch creation time
            List<Event> events = eventQueryService.getEvents(
                    ProjectEntityType.BRANCH,
                    branch.getId(),
                    EventFactory.NEW_BRANCH,
                    0,
                    1
            );
            if (events.isEmpty()) {
                infoListener.post(format("[%s][%s] No available branch creation date - keeping the branch", branch.getProject().getName(), branch.getName()));
                lastTime = Time.now();
            } else {
                lastTime = events.get(0).getSignature().getTime();
            }
        } else {
            Build build = builds.get(0);
            lastTime = build.getSignature().getTime();
        }
        // FIXME Compares with the stale and retention times
    }
}
