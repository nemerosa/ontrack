package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException;
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ValidationRunStatusService validationRunStatusService;
    private final StructureRepository structureRepository;

    @Autowired
    public StructureServiceImpl(SecurityService securityService, ValidationRunStatusService validationRunStatusService, StructureRepository structureRepository) {
        this.securityService = securityService;
        this.validationRunStatusService = validationRunStatusService;
        this.structureRepository = structureRepository;
    }

    @Override
    public Project newProject(Project project) {
        isEntityNew(project, "Project must be defined");
        securityService.checkGlobalFunction(ProjectCreation.class);
        return structureRepository.newProject(project);
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
    }

    @Override
    public Ack deleteProject(ID projectId) {
        Validate.isTrue(projectId.isSet(), "Project ID must be set");
        securityService.checkProjectFunction(projectId.getValue(), ProjectDelete.class);
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
        // OK
        return structureRepository.newBranch(branch);
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
        securityService.checkProjectFunction(branch.projectId(), ProjectEdit.class);
        structureRepository.saveBranch(branch);
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
        return structureRepository.newBuild(build);
    }

    @Override
    public Build saveBuild(Build build) {
        // Validation
        isEntityDefined(build, "Build must be defined");
        isEntityDefined(build.getBranch(), "Branch must be defined");
        isEntityDefined(build.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectEdit.class);
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
        boolean buildOk = true;
        // TODO Prefiltering without the promotions & validations
        // Promotion runs or validation runs
        if (buildFilter.needsBuildView()) {
            BuildView buildView = getBuildView(build);
            // Applies the filtering on the build view
            buildOk = buildFilter.acceptBuildView(buildView);
        }
        // TODO Final filtering
        // Adding the build
        if (buildOk) {
            builds.add(build);
        }
        // Going on? Count test: can we accept one more build?
        return buildFilter.acceptCount(builds.size() + 1);
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
        return structureRepository.newPromotionLevel(promotionLevel);
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
        // Repository
        structureRepository.setPromotionLevelImage(promotionLevelId, document);
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
        // Actual creation
        return structureRepository.newPromotionRun(promotionRun);
    }

    @Override
    public PromotionRun getPromotionRun(ID promotionRunId) {
        PromotionRun promotionRun = structureRepository.getPromotionRun(promotionRunId);
        securityService.checkProjectFunction(promotionRun.getBuild().getBranch().getProject().id(), ProjectView.class);
        return promotionRun;
    }

    @Override
    public List<PromotionRun> getLastPromotionRunsForBuild(ID buildId) {
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getLastPromotionRunsForBuild(build);
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
        return structureRepository.newValidationStamp(validationStamp);
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
        // Repository
        structureRepository.setValidationStampImage(validationStampId, document);
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
        return structureRepository.newValidationRun(validationRun);
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
        // OK
        return structureRepository.newValidationRunStatus(validationRun, runStatus);
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
