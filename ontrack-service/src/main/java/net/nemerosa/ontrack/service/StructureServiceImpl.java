package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.CachedSupplier;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException;
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException;
import net.nemerosa.ontrack.model.exceptions.ReorderingSizeException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Time;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.Entity.isEntityDefined;
import static net.nemerosa.ontrack.model.structure.Entity.isEntityNew;

@Service
@Transactional
public class StructureServiceImpl implements StructureService {

    private static final long ICON_IMAGE_SIZE_MAX = 16 * 1000L;

    private static final String[] ACCEPTED_IMAGE_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif"
    };

    private final SecurityService securityService;
    private final EventPostService eventPostService;
    private final EventFactory eventFactory;
    private final ValidationRunStatusService validationRunStatusService;
    private final StructureRepository structureRepository;

    @Autowired
    public StructureServiceImpl(SecurityService securityService, EventPostService eventPostService, EventFactory eventFactory, ValidationRunStatusService validationRunStatusService, StructureRepository structureRepository) {
        this.securityService = securityService;
        this.eventPostService = eventPostService;
        this.eventFactory = eventFactory;
        this.validationRunStatusService = validationRunStatusService;
        this.structureRepository = structureRepository;
    }

    @Override
    public Project newProject(Project project) {
        isEntityNew(project, "Project must be defined");
        securityService.checkGlobalFunction(ProjectCreation.class);
        Project newProject = structureRepository.newProject(project);
        eventPostService.post(eventFactory.newProject(newProject));
        return newProject;
    }

    @Override
    public List<Project> getProjectList() {
        List<Project> list = structureRepository.getProjectList();
        if (securityService.isGlobalFunctionGranted(ProjectList.class)) {
            return list;
        } else {
            return list.stream()
                    .filter(p -> securityService.isProjectFunctionGranted(p.id(), ProjectView.class))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Project getProject(ID projectId) {
        securityService.checkProjectFunction(projectId.getValue(), ProjectView.class);
        return structureRepository.getProject(projectId);
    }

    @Override
    public void saveProject(Project project) {
        isEntityDefined(project, "Project must be defined");
        securityService.checkProjectFunction(project.id(), ProjectEdit.class);
        structureRepository.saveProject(project);
        eventPostService.post(eventFactory.updateProject(project));
    }

    @Override
    public Ack deleteProject(ID projectId) {
        Validate.isTrue(projectId.isSet(), "Project ID must be set");
        securityService.checkProjectFunction(projectId.getValue(), ProjectDelete.class);
        eventPostService.post(eventFactory.deleteProject(getProject(projectId)));
        return structureRepository.deleteProject(projectId);
    }

    @Override
    public Branch getBranch(ID branchId) {
        Branch branch = structureRepository.getBranch(branchId);
        securityService.checkProjectFunction(branch.getProject().id(), ProjectView.class);
        return branch;
    }

    @Override
    public List<Branch> getBranchesForProject(ID projectId) {
        securityService.checkProjectFunction(projectId.getValue(), ProjectView.class);
        return structureRepository.getBranchesForProject(projectId);
    }

    @Override
    public Branch newBranch(Branch branch) {
        // Validation
        isEntityNew(branch, "Branch must be new");
        isEntityDefined(branch.getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(branch.getProject().id(), BranchCreate.class);
        // Creating the branch
        Branch newBranch = structureRepository.newBranch(branch);
        // Event
        eventPostService.post(eventFactory.newBranch(newBranch));
        // OK
        return newBranch;
    }

    @Override
    public List<BranchStatusView> getBranchStatusViews(ID projectId) {
        return getBranchesForProject(projectId).stream()
                .map(this::getBranchStatusView)
                .collect(Collectors.toList());
    }

    @Override
    public BranchStatusView getBranchStatusView(Branch branch) {
        return new BranchStatusView(
                branch,
                getLastBuildForBranch(branch),
                getPromotionLevelListForBranch(branch.getId()).stream()
                        .map(this::toPromotionView)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void saveBranch(Branch branch) {
        isEntityDefined(branch, "Branch must be defined");
        isEntityDefined(branch.getProject(), "Project must be defined");
        securityService.checkProjectFunction(branch.projectId(), BranchEdit.class);
        structureRepository.saveBranch(branch);
        eventPostService.post(eventFactory.updateBranch(branch));
    }

    @Override
    public Ack deleteBranch(ID branchId) {
        Validate.isTrue(branchId.isSet(), "Branch ID must be set");
        Branch branch = getBranch(branchId);
        securityService.checkProjectFunction(branch.projectId(), BranchDelete.class);
        eventPostService.post(eventFactory.deleteBranch(branch));
        return structureRepository.deleteBranch(branchId);
    }

    protected PromotionView toPromotionView(PromotionLevel promotionLevel) {
        // Gets the last build having this promotion level
        PromotionRun promotionRun = getLastPromotionRunForPromotionLevel(promotionLevel);
        // OK
        return new PromotionView(
                promotionLevel,
                promotionRun
        );
    }

    @Override
    public PromotionRun getLastPromotionRunForPromotionLevel(PromotionLevel promotionLevel) {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView.class);
        return structureRepository.getLastPromotionRunForPromotionLevel(promotionLevel);
    }

    @Override
    public PromotionRunView getPromotionRunView(PromotionLevel promotionLevel) {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView.class);
        return new PromotionRunView(
                promotionLevel,
                structureRepository.getPromotionRunsForPromotionLevel(promotionLevel)
        );
    }

    @Override
    public Ack deletePromotionRun(ID promotionRunId) {
        Validate.isTrue(promotionRunId.isSet(), "Promotion run ID must be set");
        PromotionRun promotionRun = getPromotionRun(promotionRunId);
        securityService.checkProjectFunction(promotionRun, PromotionRunDelete.class);
        eventPostService.post(eventFactory.deletePromotionRun(promotionRun));
        return structureRepository.deletePromotionRun(promotionRunId);
    }

    @Override
    public Build getLastBuildForBranch(Branch branch) {
        // Checks the accesses
        securityService.checkProjectFunction(branch.projectId(), ProjectView.class);
        // Gets the last build
        return structureRepository.getLastBuildForBranch(branch);
    }

    @Override
    public Ack deleteBuild(ID buildId) {
        Validate.isTrue(buildId.isSet(), "Build ID must be set");
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.projectId(), BuildDelete.class);
        eventPostService.post(eventFactory.deleteBuild(build));
        return structureRepository.deleteBuild(buildId);
    }

    @Override
    public Build newBuild(Build build) {
        // Validation
        isEntityNew(build, "Build must be new");
        isEntityDefined(build.getBranch(), "Branch must be defined");
        isEntityDefined(build.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(build.getBranch().getProject().id(), BuildCreate.class);
        // Repository
        Build newBuild = structureRepository.newBuild(build);
        // Event
        eventPostService.post(eventFactory.newBuild(newBuild));
        // OK
        return newBuild;
    }

    @Override
    public Build saveBuild(Build build) {
        // Validation
        isEntityDefined(build, "Build must be defined");
        isEntityDefined(build.getBranch(), "Branch must be defined");
        isEntityDefined(build.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(build.getBranch().getProject().id(), BuildEdit.class);
        // TODO Event
        // Repository
        return structureRepository.saveBuild(build);
    }

    @Override
    public Build getBuild(ID buildId) {
        Build build = structureRepository.getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return build;
    }

    @Override
    public List<Build> getFilteredBuilds(ID branchId, BuildFilter buildFilter) {
        // Gets the branch
        Branch branch = getBranch(branchId);
        // Collects the builds associated with this predicate
        List<Build> builds = new ArrayList<>();
        structureRepository.builds(branch, build -> filterBuild(builds, branch, build, buildFilter));
        return builds;
    }

    /**
     * @param builds      List of builds already added in the list
     * @param branch      Branch
     * @param build       Build to test
     * @param buildFilter Filter definition
     * @return True is OK to go on with other builds
     */
    private boolean filterBuild(List<Build> builds, Branch branch, Build build, BuildFilter buildFilter) {
        // Calls the filter
        BuildFilterResult result = buildFilter.filter(
                builds,
                branch,
                build,
                CachedSupplier.of(() -> getBuildView(build))
        );
        // Adding the build
        if (result.isAccept()) {
            builds.add(build);
        }
        // Going on?
        return result.isGoingOn();
    }

    @Override
    public List<ValidationStampRunView> getValidationStampRunViewsForBuild(Build build) {
        // Gets all validation stamps
        List<ValidationStamp> stamps = getValidationStampListForBranch(build.getBranch().getId());
        // Gets all runs for this build
        List<ValidationRun> runs = getValidationRunsForBuild(build.getId());
        // Gets the validation stamp run views
        return stamps.stream()
                .map(stamp -> getValidationStampRunView(runs, stamp))
                .collect(Collectors.toList());
    }

    protected ValidationStampRunView getValidationStampRunView(List<ValidationRun> runs, ValidationStamp stamp) {
        return new ValidationStampRunView(
                stamp,
                runs.stream()
                        .filter(run -> run.getValidationStamp().id() == stamp.id())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<PromotionLevel> getPromotionLevelListForBranch(ID branchId) {
        Branch branch = getBranch(branchId);
        securityService.checkProjectFunction(branch.getProject().id(), ProjectView.class);
        return structureRepository.getPromotionLevelListForBranch(branchId);
    }

    @Override
    public PromotionLevel newPromotionLevel(PromotionLevel promotionLevel) {
        // Validation
        isEntityNew(promotionLevel, "Promotion level must be new");
        isEntityDefined(promotionLevel.getBranch(), "Branch must be defined");
        isEntityDefined(promotionLevel.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(promotionLevel.getBranch().getProject().id(), PromotionLevelCreate.class);
        // Repository
        PromotionLevel newPromotionLevel = structureRepository.newPromotionLevel(promotionLevel);
        // Event
        eventPostService.post(eventFactory.newPromotionLevel(newPromotionLevel));
        // OK
        return newPromotionLevel;
    }

    @Override
    public PromotionLevel getPromotionLevel(ID promotionLevelId) {
        PromotionLevel promotionLevel = structureRepository.getPromotionLevel(promotionLevelId);
        securityService.checkProjectFunction(promotionLevel.getBranch().getProject().id(), ProjectView.class);
        return promotionLevel;
    }

    @Override
    public Document getPromotionLevelImage(ID promotionLevelId) {
        // Checks access
        getPromotionLevel(promotionLevelId);
        // Repository access
        return structureRepository.getPromotionLevelImage(promotionLevelId);
    }

    @Override
    public void setPromotionLevelImage(ID promotionLevelId, Document document) {
        checkImage(document);
        // Checks access
        PromotionLevel promotionLevel = getPromotionLevel(promotionLevelId);
        securityService.checkProjectFunction(promotionLevel.getBranch().getProject().id(), PromotionLevelEdit.class);
        // TODO Event
        // Repository
        structureRepository.setPromotionLevelImage(promotionLevelId, document);
    }

    @Override
    public void savePromotionLevel(PromotionLevel promotionLevel) {
        // Validation
        isEntityDefined(promotionLevel, "Promotion level must be defined");
        isEntityDefined(promotionLevel.getBranch(), "Branch must be defined");
        isEntityDefined(promotionLevel.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelEdit.class);
        // TODO Event
        // Repository
        structureRepository.savePromotionLevel(promotionLevel);
    }

    @Override
    public Ack deletePromotionLevel(ID promotionLevelId) {
        Validate.isTrue(promotionLevelId.isSet(), "Promotion level ID must be set");
        PromotionLevel promotionLevel = getPromotionLevel(promotionLevelId);
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelDelete.class);
        // TODO Event
        return structureRepository.deletePromotionLevel(promotionLevelId);
    }

    @Override
    public void reorderPromotionLevels(ID branchId, Reordering reordering) {
        // Loads the branch
        Branch branch = getBranch(branchId);
        // Checks the access rights
        securityService.checkProjectFunction(branch.projectId(), PromotionLevelEdit.class);
        // Loads the promotion levels
        List<PromotionLevel> promotionLevels = getPromotionLevelListForBranch(branchId);
        // Checks the size
        if (reordering.getIds().size() != promotionLevels.size()) {
            throw new ReorderingSizeException("The reordering request should have the same number of IDs as the number" +
                    " of the promotion levels");
        }
        // TODO Event
        // Actual reordering
        structureRepository.reorderPromotionLevels(branchId, reordering);
    }

    @Override
    public PromotionRun newPromotionRun(PromotionRun promotionRun) {
        // Validation
        isEntityNew(promotionRun, "Promotion run must be new");
        isEntityDefined(promotionRun.getBuild(), "Build must be defined");
        isEntityDefined(promotionRun.getPromotionLevel(), "Promotion level must be defined");
        Validate.isTrue(promotionRun.getPromotionLevel().getBranch().id() == promotionRun.getBuild().getBranch().id(),
                "Promotion for a promotion level can be done only on the same branch than the build.");
        // Checks the authorization
        securityService.checkProjectFunction(promotionRun.getBuild().getBranch().getProject().id(), PromotionRunCreate.class);
        // If the promotion run's time is not defined, takes the current date
        PromotionRun promotionRunToSave;
        LocalDateTime time = promotionRun.getSignature().getTime();
        if (time == null) {
            promotionRunToSave = PromotionRun.of(
                    promotionRun.getBuild(),
                    promotionRun.getPromotionLevel(),
                    promotionRun.getSignature().withTime(Time.now()),
                    promotionRun.getDescription()
            );
        } else {
            promotionRunToSave = promotionRun;
        }
        // Actual creation
        PromotionRun newPromotionRun = structureRepository.newPromotionRun(promotionRunToSave);
        // Event
        eventPostService.post(eventFactory.newPromotionRun(newPromotionRun));
        // OK
        return newPromotionRun;
    }

    @Override
    public PromotionRun getPromotionRun(ID promotionRunId) {
        PromotionRun promotionRun = structureRepository.getPromotionRun(promotionRunId);
        securityService.checkProjectFunction(promotionRun.getBuild().getBranch().getProject().id(), ProjectView.class);
        // TODO Event
        return promotionRun;
    }

    @Override
    public List<PromotionRun> getLastPromotionRunsForBuild(ID buildId) {
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getLastPromotionRunsForBuild(build);
    }

    @Override
    public Optional<PromotionRun> getLastPromotionRunForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.getLastPromotionRun(build, promotionLevel);
    }

    @Override
    public List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(Build build, PromotionLevel promotionLevel) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel);
    }

    @Override
    public List<ValidationStamp> getValidationStampListForBranch(ID branchId) {
        Branch branch = getBranch(branchId);
        securityService.checkProjectFunction(branch.getProject().id(), ProjectView.class);
        return structureRepository.getValidationStampListForBranch(branchId);
    }

    @Override
    public ValidationStamp newValidationStamp(ValidationStamp validationStamp) {
        // Validation
        isEntityNew(validationStamp, "Validation stamp must be new");
        isEntityDefined(validationStamp.getBranch(), "Branch must be defined");
        isEntityDefined(validationStamp.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(validationStamp.getBranch().getProject().id(), ValidationStampCreate.class);
        // Repository
        ValidationStamp newValidationStamp = structureRepository.newValidationStamp(validationStamp);
        // Event
        eventPostService.post(eventFactory.newValidationStamp(newValidationStamp));
        // OK
        return newValidationStamp;
    }

    @Override
    public ValidationStamp getValidationStamp(ID validationStampId) {
        ValidationStamp validationStamp = structureRepository.getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.getBranch().getProject().id(), ProjectView.class);
        return validationStamp;
    }

    @Override
    public Optional<ValidationStamp> findValidationStampByName(String project, String branch, String validationStamp) {
        return structureRepository.getValidationStampByName(project, branch, validationStamp)
                .filter(vs -> securityService.isProjectFunctionGranted(vs, ProjectView.class));
    }

    @Override
    public Optional<Build> findBuildByName(String project, String branch, String build) {
        return structureRepository.getBuildByName(project, branch, build)
                .filter(b -> securityService.isProjectFunctionGranted(b, ProjectView.class));
    }

    @Override
    public Optional<Build> findBuildAfterUsingNumericForm(ID branchId, String buildName) {
        if (StringUtils.isNumeric(buildName)) {
            return structureRepository.findBuildAfterUsingNumericForm(branchId, buildName);
        } else {
            throw new IllegalArgumentException("Build name is expected to be numeric: " + buildName);
        }
    }

    @Override
    public BuildView getBuildView(Build build) {
        return new BuildView(
                build,
                getLastPromotionRunsForBuild(build.getId()),
                getValidationStampRunViewsForBuild(build)
        );
    }

    @Override
    public Document getValidationStampImage(ID validationStampId) {
        // Checks access
        getValidationStamp(validationStampId);
        // Repository access
        return structureRepository.getValidationStampImage(validationStampId);
    }

    @Override
    public void setValidationStampImage(ID validationStampId, Document document) {
        // Checks the image type
        checkImage(document);
        // Checks access
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.getBranch().getProject().id(), ValidationStampEdit.class);
        // TODO Event
        // Repository
        structureRepository.setValidationStampImage(validationStampId, document);
    }

    @Override
    public void saveValidationStamp(ValidationStamp validationStamp) {
        // Validation
        isEntityDefined(validationStamp, "Validation stamp must be defined");
        isEntityDefined(validationStamp.getBranch(), "Branch must be defined");
        isEntityDefined(validationStamp.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampEdit.class);
        // TODO Event
        // Repository
        structureRepository.saveValidationStamp(validationStamp);
    }

    @Override
    public Ack deleteValidationStamp(ID validationStampId) {
        Validate.isTrue(validationStampId.isSet(), "Validation stamp ID must be set");
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampDelete.class);
        // TODO Event
        return structureRepository.deleteValidationStamp(validationStampId);
    }

    @Override
    public void reorderValidationStamps(ID branchId, Reordering reordering) {
        // Loads the branch
        Branch branch = getBranch(branchId);
        // Checks the access rights
        securityService.checkProjectFunction(branch.projectId(), ValidationStampEdit.class);
        // Loads the validation stamps
        List<ValidationStamp> validationStamps = getValidationStampListForBranch(branchId);
        // Checks the size
        if (reordering.getIds().size() != validationStamps.size()) {
            throw new ReorderingSizeException("The reordering request should have the same number of IDs as the number" +
                    " of the validation stamps");
        }
        // TODO Event
        // Actual reordering
        structureRepository.reorderValidationStamps(branchId, reordering);
    }

    @Override
    public ValidationRun newValidationRun(ValidationRun validationRun) {
        // Validation
        isEntityNew(validationRun, "Validation run must be new");
        isEntityDefined(validationRun.getBuild(), "Build must be defined");
        isEntityDefined(validationRun.getValidationStamp(), "Validation stamp must be defined");
        Validate.isTrue(validationRun.getValidationStamp().getBranch().id() == validationRun.getBuild().getBranch().id(),
                "Validation run for a validation stamp can be done only on the same branch than the build.");
        // Checks the authorization
        securityService.checkProjectFunction(validationRun.getBuild().getBranch().getProject().id(), ValidationRunCreate.class);
        // Actual creation
        ValidationRun newValidationRun = structureRepository.newValidationRun(validationRun);
        // Event
        eventPostService.post(eventFactory.newValidationRun(newValidationRun));
        // OK
        return newValidationRun;
    }

    @Override
    public ValidationRun getValidationRun(ID validationRunId) {
        ValidationRun validationRun = structureRepository.getValidationRun(validationRunId);
        securityService.checkProjectFunction(validationRun.getBuild().getBranch().getProject().id(), ProjectView.class);
        return validationRun;
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuild(ID buildId) {
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getValidationRunsForBuild(build);
    }

    @Override
    public List<ValidationRun> getValidationRunsForValidationStamp(ID validationStampId, int offset, int count) {
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getValidationRunsForValidationStamp(validationStamp, offset, count);
    }

    @Override
    public ValidationRun newValidationRunStatus(ValidationRun validationRun, ValidationRunStatus runStatus) {
        // Entity check
        Entity.isEntityDefined(validationRun, "Validation run must be defined");
        // Security check
        securityService.checkProjectFunction(validationRun.getBuild().getBranch().getProject().id(), ValidationRunStatusChange.class);
        // Transition check
        validationRunStatusService.checkTransition(validationRun.getLastStatus().getStatusID(), runStatus.getStatusID());
        // Creation
        ValidationRun newValidationRun = structureRepository.newValidationRunStatus(validationRun, runStatus);
        // Event
        eventPostService.post(eventFactory.newValidationRunStatus(newValidationRun));
        // OK
        return newValidationRun;
    }

    @Override
    public Optional<Project> findProjectByName(String project) {
        return structureRepository.getProjectByName(project)
                .filter(p -> securityService.isProjectFunctionGranted(p.id(), ProjectView.class));
    }

    @Override
    public Optional<Branch> findBranchByName(String project, String branch) {
        return structureRepository.getBranchByName(project, branch)
                .filter(b -> securityService.isProjectFunctionGranted(b.projectId(), ProjectView.class));
    }

    @Override
    public Optional<PromotionLevel> findPromotionLevelByName(String project, String branch, String promotionLevel) {
        return structureRepository.getPromotionLevelByName(project, branch, promotionLevel)
                .filter(pl -> securityService.isProjectFunctionGranted(pl.projectId(), ProjectView.class));
    }

    protected void checkImage(Document document) {
        // Checks the image type
        if (document != null && !ArrayUtils.contains(ACCEPTED_IMAGE_TYPES, document.getType())) {
            throw new ImageTypeNotAcceptedException(document.getType(), ACCEPTED_IMAGE_TYPES);
        }
        // Checks the image length
        int size = document != null ? document.getContent().length : 0;
        if (size > ICON_IMAGE_SIZE_MAX) {
            throw new ImageFileSizeException(size, ICON_IMAGE_SIZE_MAX);
        }
    }
}
