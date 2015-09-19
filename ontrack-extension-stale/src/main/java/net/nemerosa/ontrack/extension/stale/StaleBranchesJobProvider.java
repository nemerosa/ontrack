package net.nemerosa.ontrack.extension.stale;

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Detection and management of stale branches.
 */
@Component
public class StaleBranchesJobProvider implements JobProvider {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final EventQueryService eventQueryService;

    @Autowired
    public StaleBranchesJobProvider(StructureService structureService, PropertyService propertyService, EventQueryService eventQueryService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.eventQueryService = eventQueryService;
    }

    @Override
    public Collection<Job> getJobs() {
        // Gets all projects...
        return structureService.getProjectList().stream()
                // ... which have a StaleProperty
                .filter(project -> propertyService.hasProperty(project, StalePropertyType.class))
                        // ... and associates a job with them
                .map(this::createStaleJob)
                        // OK
                .collect(Collectors.toList());
    }

    protected Job createStaleJob(Project project) {
        return new Job() {
            @Override
            public String getCategory() {
                return "StaleBranches";
            }

            @Override
            public String getId() {
                return project.getId().toString();
            }

            @Override
            public String getDescription() {
                return "Detection and management of stale branches for " + project.getName();
            }

            @Override
            public boolean isDisabled() {
                return project.isDisabled();
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
                return new RunnableJobTask(jobInfoListener -> detectAndManageStaleBranches(jobInfoListener, project));
            }
        };
    }

    protected void detectAndManageStaleBranches(JobInfoListener infoListener, Project project) {
        // Gets the stale property for the project
        propertyService.getProperty(project, StalePropertyType.class).option().ifPresent(property -> {
            // Disabling and deletion times
            int disablingDuration = property.getDisablingDuration();
            int deletionDuration = property.getDeletingDuration();
            if (disablingDuration <= 0) {
                infoListener.post(format("[%s] No disabling time being set - exiting.", project.getName()));
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
                // Logging
                infoListener.post(format("[%s] Disabling time: %s", project.getName(), disablingTime));
                infoListener.post(format("[%s] Deletion time: %s", project.getName(), deletionTime));
                // Going on with the scan of the project
                infoListener.post(format("[%s] Scanning project for stale branches", project.getName()));
                structureService.getBranchesForProject(project.getId()).forEach(
                        branch -> detectAndManageStaleBranch(infoListener, branch, disablingTime, deletionTime)
                );
            }
        });
    }

    protected void detectAndManageStaleBranch(JobInfoListener infoListener, Branch branch, LocalDateTime disablingTime, Optional<LocalDateTime> deletionTime) {
        infoListener.post(format("[%s][%s] Scanning branch for staleness", branch.getProject().getName(), branch.getName()));
        // Templates are excluded
        if (branch.getType() == BranchType.TEMPLATE_DEFINITION) {
            infoListener.post(format("[%s][%s] Branch templates are not eligible for staleness", branch.getProject().getName(), branch.getName()));
            return;
        }
        // Last date
        LocalDateTime lastTime;
        // Last build on this branch
        Optional<Build> oBuild = structureService.getLastBuild(branch.getId());
        if (!oBuild.isPresent()) {
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
            Build build = oBuild.get();
            lastTime = build.getSignature().getTime();
        }
        // Logging
        infoListener.post(format("[%s][%s] Branch last build activity: %s", branch.getProject().getName(), branch.getName(), lastTime));
        // Deletion?
        if (deletionTime.isPresent() && deletionTime.get().compareTo(lastTime) > 0) {
            infoListener.post(format("[%s][%s] Branch due for deletion", branch.getProject().getName(), branch.getName()));
            structureService.deleteBranch(branch.getId());
        } else if (disablingTime.compareTo(lastTime) > 0 && !branch.isDisabled()) {
            infoListener.post(format("[%s][%s] Branch due for staleness - disabling", branch.getProject().getName(), branch.getName()));
            structureService.saveBranch(
                    branch.withDisabled(true)
            );
        } else {
            infoListener.post(format("[%s][%s] Not touching the branch", branch.getProject().getName(), branch.getName()));
        }
    }
}
