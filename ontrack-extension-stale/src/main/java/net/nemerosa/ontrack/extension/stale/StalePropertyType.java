package net.nemerosa.ontrack.extension.stale;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.extension.support.AbstractPropertyType;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.JobProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StalePropertyType extends AbstractPropertyType<StaleProperty> implements JobProvider {

    public static final JobType STALE_BRANCH_JOB =
            JobCategory.of("cleanup").withName("Cleanup")
                    .getType("stale-branches").withName("Stale branches cleanup");

    private final Logger logger = LoggerFactory.getLogger(StalePropertyType.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final EventQueryService eventQueryService;
    private final JobScheduler jobScheduler;

    @Autowired
    public StalePropertyType(StaleExtensionFeature extensionFeature, StructureService structureService, PropertyService propertyService, EventQueryService eventQueryService, JobScheduler jobScheduler) {
        super(extensionFeature);
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.eventQueryService = eventQueryService;
        this.jobScheduler = jobScheduler;
    }

    @Override
    public String getName() {
        return "Stale branches";
    }

    @Override
    public String getDescription() {
        return "Allows to disable or delete stale branches";
    }

    @Override
    public Set<ProjectEntityType> getSupportedEntityTypes() {
        return EnumSet.of(ProjectEntityType.PROJECT);
    }

    @Override
    public boolean canEdit(ProjectEntity entity, SecurityService securityService) {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig.class);
    }

    @Override
    public boolean canView(ProjectEntity entity, SecurityService securityService) {
        return true;
    }

    @Override
    public Form getEditionForm(ProjectEntity entity, StaleProperty value) {
        return Form.create()
                .with(
                        Int.of("disablingDuration")
                                .label("Disabling branches after N (days)")
                                .min(0)
                                .help("Number of days of inactivity after a branch is disabled. 0 means that " +
                                        "the branch won't ever be disabled automatically.")
                                .value(value != null ? value.getDisablingDuration() : 0)
                )
                .with(
                        Int.of("deletingDuration")
                                .label("Deleting branches after N (days) more")
                                .min(0)
                                .help("Number of days of inactivity after a branch is deleted, after it has been" +
                                        "disabled automatically. 0 means that " +
                                        "the branch won't ever be deleted automatically.")
                                .value(value != null ? value.getDeletingDuration() : 0)
                )
                .with(
                        MultiStrings.of("promotionsToKeep")
                                .label("Promotions to keep")
                                .help("List of promotion levels which prevent a branch to be disabled or deleted")
                                .value(value != null ? value.getPromotionsToKeep() : Collections.emptyList())
                )
                ;
    }

    @Override
    public StaleProperty fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public StaleProperty fromStorage(JsonNode node) {
        return parse(node, StaleProperty.class);
    }

    @Override
    public String getSearchKey(StaleProperty value) {
        return null;
    }

    @Override
    public StaleProperty replaceValue(StaleProperty value, Function<String, String> replacementFunction) {
        return value;
    }

    @Override
    public void onPropertyChanged(ProjectEntity entity, StaleProperty value) {
        if (propertyService.hasProperty(entity.getProject(), StalePropertyType.class)) {
            JobRegistration job = createStaleJob(entity.getProject());
            jobScheduler.schedule(
                    job.getJob(),
                    job.getSchedule()
            );
        }
    }

    @Override
    public void onPropertyDeleted(ProjectEntity entity, StaleProperty oldValue) {
        unscheduleStaleBranchJob((Project) entity);
    }

    @Override
    public Collection<JobRegistration> getStartingJobs() {
        // Gets all projects...
        return structureService.getProjectList().stream()
                // ... which have a StaleProperty
                .filter(project -> propertyService.hasProperty(project, StalePropertyType.class))
                // ... and associates a job with them
                .map(this::createStaleJob)
                // OK
                .collect(Collectors.toList());
    }

    protected void unscheduleStaleBranchJob(Project project) {
        jobScheduler.unschedule(getStaleJobKey(project));
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

    protected void detectAndManageStaleBranches(JobRunListener runListener, Project project) {
        // Gets the stale property for the project
        propertyService.getProperty(project, StalePropertyType.class).option().ifPresent(property -> {
            // Disabling and deletion times
            int disablingDuration = property.getDisablingDuration();
            int deletionDuration = property.getDeletingDuration();
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
                        branch -> detectAndManageStaleBranch(branch, disablingTime, deletionTime.orElse(null))
                );
            }
        });
    }

    protected void detectAndManageStaleBranch(Branch branch, LocalDateTime disablingTime, LocalDateTime deletionTime) {
        trace(branch.getProject(), "[%s] Scanning branch for staleness", branch.getName());
        // Templates are excluded
        if (branch.getType() == BranchType.TEMPLATE_DEFINITION) {
            trace(branch.getProject(), "[%s] Branch templates are not eligible for staleness", branch.getName());
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
