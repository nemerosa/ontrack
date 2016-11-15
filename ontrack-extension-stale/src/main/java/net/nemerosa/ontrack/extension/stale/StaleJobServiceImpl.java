package net.nemerosa.ontrack.extension.stale;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.structure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Component
public class StaleJobServiceImpl implements StaleJobService {

    private final Logger logger = LoggerFactory.getLogger(StaleJobServiceImpl.class);

    public static final JobType STALE_BRANCH_JOB =
            JobCategory.of("cleanup").withName("Cleanup")
                    .getType("stale-branches").withName("Stale branches cleanup");

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final EventQueryService eventQueryService;

    @Autowired
    public StaleJobServiceImpl(StructureService structureService, PropertyService propertyService, EventQueryService eventQueryService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.eventQueryService = eventQueryService;
    }

    @Override
    public Stream<JobRegistration> collectJobRegistrations() {
        // Gets all projects...
        return structureService.getProjectList().stream()
                // ... which have a StaleProperty
                .filter(project -> propertyService.hasProperty(project, StalePropertyType.class))
                // ... and associates a job with them
                .map(this::createStaleJob);
    }

    protected JobRegistration createStaleJob(Project project) {
        return JobRegistration.of(
                new Job() {

                    @Override
                    public JobKey getKey() {
                        return getStaleJobKey(project);
                    }

                    @Override
                    public JobRun getTask() {
                        return runListener -> detectAndManageStaleBranches(runListener, project);
                    }

                    @Override
                    public String getDescription() {
                        return "Detection and management of stale branches for " + project.getName();
                    }

                    @Override
                    public boolean isDisabled() {
                        return project.isDisabled();
                    }

                    @Override
                    public boolean isValid() {
                        return propertyService.hasProperty(project, StalePropertyType.class);
                    }
                }
        ).withSchedule(Schedule.EVERY_DAY);
    }

    protected JobKey getStaleJobKey(Project project) {
        return STALE_BRANCH_JOB.getKey(
                String.valueOf(project.getId())
        );
    }

    protected void trace(Project project, String pattern, Object... arguments) {
        logger.debug(String.format(
                "[%s] %s",
                project.getName(),
                String.format(pattern, arguments)
        ));
    }

    @Override
    public void detectAndManageStaleBranches(JobRunListener runListener, Project project) {
        // Gets the stale property for the project
        propertyService.getProperty(project, StalePropertyType.class).option().ifPresent(property -> {
            // Disabling and deletion times
            int disablingDuration = property.getDisablingDuration();
            int deletionDuration = property.getDeletingDuration();
            List<String> promotionsToKeep = property.getPromotionsToKeep();
            if (disablingDuration <= 0) {
                trace(project, "No disabling time being set - exiting.");
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
                trace(project, "Disabling time: %s", disablingTime);
                trace(project, "Deletion time: %s", deletionTime);
                // Going on with the scan of the project
                runListener.message("Scanning %s project for stale branches", project.getName());
                trace(project, "Scanning project for stale branches");
                structureService.getBranchesForProject(project.getId()).forEach(
                        branch -> detectAndManageStaleBranch(branch, disablingTime, deletionTime.orElse(null), promotionsToKeep)
                );
            }
        });
    }

    @Override
    public void detectAndManageStaleBranch(Branch branch, LocalDateTime disablingTime, LocalDateTime deletionTime, List<String> promotionsToKeep) {
        trace(branch.getProject(), "[%s] Scanning branch for staleness", branch.getName());
        // Templates are excluded
        if (branch.getType() == BranchType.TEMPLATE_DEFINITION) {
            trace(branch.getProject(), "[%s] Branch templates are not eligible for staleness", branch.getName());
            return;
        }
        // Indexation of promotion levels to protect
        Set<String> promotionsToProtect;
        if (promotionsToKeep != null) {
            promotionsToProtect = new HashSet<>(promotionsToKeep);
        } else {
            promotionsToProtect = Collections.emptySet();
        }
        // Gets the last promotions for this branch
        List<PromotionView> lastPromotions = structureService.getBranchStatusView(branch).getPromotions();
        boolean isProtected = lastPromotions.stream()
                .anyMatch(promotionView -> promotionView.getPromotionRun() != null
                        && promotionsToProtect.contains(promotionView.getPromotionLevel().getName()));
        if (isProtected) {
            trace(branch.getProject(), "[%s] Branch is promoted and is not eligible for staleness", branch.getName());
            return;
        }
        // Last date
        LocalDateTime lastTime;
        // Last build on this branch
        Optional<Build> oBuild = structureService.getLastBuild(branch.getId());
        if (!oBuild.isPresent()) {
            trace(branch.getProject(), "[%s] No available build - taking branch's creation time", branch.getName());
            // Takes the branch creation time
            List<Event> events = eventQueryService.getEvents(
                    ProjectEntityType.BRANCH,
                    branch.getId(),
                    EventFactory.NEW_BRANCH,
                    0,
                    1
            );
            if (events.isEmpty()) {
                trace(branch.getProject(), "[%s] No available branch creation date - keeping the branch", branch.getName());
                lastTime = Time.now();
            } else {
                lastTime = events.get(0).getSignature().getTime();
            }
        } else {
            Build build = oBuild.get();
            lastTime = build.getSignature().getTime();
        }
        // Logging
        trace(branch.getProject(), "[%s] Branch last build activity: %s", branch.getName(), lastTime);
        // Deletion?
        if (deletionTime != null && deletionTime.compareTo(lastTime) > 0) {
            trace(branch.getProject(), "[%s] Branch due for deletion", branch.getName());
            structureService.deleteBranch(branch.getId());
        } else if (disablingTime.compareTo(lastTime) > 0 && !branch.isDisabled()) {
            trace(branch.getProject(), "[%s] Branch due for staleness - disabling", branch.getName());
            structureService.saveBranch(
                    branch.withDisabled(true)
            );
        } else {
            trace(branch.getProject(), "[%s] Not touching the branch", branch.getName());
        }
    }
}
