package net.nemerosa.ontrack.service;

import com.google.common.collect.Iterables;
import net.nemerosa.ontrack.common.CachedSupplier;
import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.common.Utils;
import net.nemerosa.ontrack.extension.api.BuildValidationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.exceptions.*;
import net.nemerosa.ontrack.model.extension.PromotionLevelPropertyType;
import net.nemerosa.ontrack.model.extension.ValidationStampPropertyType;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService;
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.PropertyServiceHelper;
import net.nemerosa.ontrack.repository.StructureRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.Entity.isEntityDefined;
import static net.nemerosa.ontrack.model.structure.Entity.isEntityNew;
import static net.nemerosa.ontrack.service.ImageHelper.checkImage;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional
public class StructureServiceImpl implements StructureService {

    private final SecurityService securityService;
    private final EventPostService eventPostService;
    private final EventFactory eventFactory;
    private final ValidationRunStatusService validationRunStatusService;
    private final StructureRepository structureRepository;
    private final ExtensionManager extensionManager;
    private final PropertyService propertyService;
    private final PredefinedPromotionLevelService predefinedPromotionLevelService;
    private final PredefinedValidationStampService predefinedValidationStampService;
    private final DecorationService decorationService;
    private final ProjectFavouriteService projectFavouriteService;

    @Autowired
    public StructureServiceImpl(SecurityService securityService, EventPostService eventPostService, EventFactory eventFactory, ValidationRunStatusService validationRunStatusService, StructureRepository structureRepository, ExtensionManager extensionManager, PropertyService propertyService, PredefinedPromotionLevelService predefinedPromotionLevelService, PredefinedValidationStampService predefinedValidationStampService, DecorationService decorationService, ProjectFavouriteService projectFavouriteService) {
        this.securityService = securityService;
        this.eventPostService = eventPostService;
        this.eventFactory = eventFactory;
        this.validationRunStatusService = validationRunStatusService;
        this.structureRepository = structureRepository;
        this.extensionManager = extensionManager;
        this.propertyService = propertyService;
        this.predefinedPromotionLevelService = predefinedPromotionLevelService;
        this.predefinedValidationStampService = predefinedValidationStampService;
        this.decorationService = decorationService;
        this.projectFavouriteService = projectFavouriteService;
    }

    @Override
    public List<ProjectStatusView> getProjectStatusViews() {
        return getProjectList().stream()
                .map(project -> new ProjectStatusView(
                        project,
                        decorationService.getDecorations(project),
                        getBranchStatusViews(project.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectStatusView> getProjectStatusViewsForFavourites() {
        return getProjectFavourites().stream()
                .map(project -> new ProjectStatusView(
                        project,
                        decorationService.getDecorations(project),
                        getBranchStatusViews(project.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getProjectFavourites() {
        // Gets the list of all authorised projects...
        return getProjectList().stream()
                // .. filtered using the preferences
                .filter(projectFavouriteService::isProjectFavourite)
                // .. ok
                .collect(Collectors.toList());
    }

    @Override
    public Project newProject(Project project) {
        isEntityNew(project, "Project must be defined");
        securityService.checkGlobalFunction(ProjectCreation.class);
        Project newProject = structureRepository.newProject(project.withSignature(securityService.getCurrentSignature()));
        eventPostService.post(eventFactory.newProject(newProject));
        return newProject;
    }

    @Override
    public List<Project> getProjectList() {
        SecuritySettings securitySettings = securityService.getSecuritySettings();
        List<Project> list = structureRepository.getProjectList();
        if (securitySettings.isGrantProjectViewToAll() || securityService.isGlobalFunctionGranted(ProjectList.class)) {
            return list;
        } else if (securityService.isLogged()) {
            return list.stream()
                    .filter(p -> securityService.isProjectFunctionGranted(p.id(), ProjectView.class))
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Authentication is required.");
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
        Branch newBranch = structureRepository.newBranch(branch.withSignature(securityService.getCurrentSignature()));
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
                decorationService.getDecorations(branch),
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
    public Optional<PromotionRun> getEarliestPromotionRunAfterBuild(PromotionLevel promotionLevel, Build build) {
        securityService.checkProjectFunction(promotionLevel.projectId(), ProjectView.class);
        return structureRepository.getEarliestPromotionRunAfterBuild(promotionLevel, build);
    }

    @Override
    public List<PromotionRun> getPromotionRunsForPromotionLevel(ID promotionLevelId) {
        PromotionLevel promotionLevel = getPromotionLevel(promotionLevelId);
        return structureRepository.getPromotionRunsForPromotionLevel(promotionLevel);
    }

    @Override
    public Build getLastBuildForBranch(Branch branch) {
        // Checks the accesses
        securityService.checkProjectFunction(branch.projectId(), ProjectView.class);
        // Gets the last build
        return structureRepository.getLastBuildForBranch(branch);
    }

    @Override
    public int getBuildCount(Branch branch) {
        return structureRepository.getBuildCount(branch);
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
    public Optional<Build> getPreviousBuild(ID buildId) {
        return structureRepository.getPreviousBuild(getBuild(buildId));
    }

    @Override
    public Optional<Build> getNextBuild(ID buildId) {
        return structureRepository.getNextBuild(getBuild(buildId));
    }

    protected void validateBuild(Build build) {
        extensionManager.getExtensions(BuildValidationExtension.class).forEach(
                x -> x.validateBuild(build)
        );
    }

    @Override
    public Build newBuild(Build build) {
        // Validation
        isEntityNew(build, "Build must be new");
        isEntityDefined(build.getBranch(), "Branch must be defined");
        isEntityDefined(build.getBranch().getProject(), "Project must be defined");
        // Branch must not be a template definition
        if (getBranch(build.getBranch().getId()).getType() == BranchType.TEMPLATE_DEFINITION) {
            throw new BranchTemplateCannotHaveBuildException(build.getBranch().getName());
        }
        // Security
        securityService.checkProjectFunction(build.getBranch().getProject().id(), BuildCreate.class);
        // Build validation
        validateBuild(build);
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
        // Signature change check
        validationSignatureChange(build);
        // Build validation
        validateBuild(build);
        // Repository
        Build savedBuild = structureRepository.saveBuild(build);
        // Event
        eventPostService.post(eventFactory.updateBuild(savedBuild));
        // OK
        return getBuild(build.getId());
    }

    private void validationSignatureChange(Build build) {
        // Get the original build signature
        Build orig = getBuild(build.getId());
        // Compares the signatures
        if (!Objects.equals(orig.getSignature(), build.getSignature())) {
            // Checks the authorisation
            securityService.checkProjectFunction(build, ProjectEdit.class);
        }
    }

    @Override
    public Build getBuild(ID buildId) {
        Build build = structureRepository.getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return build;
    }

    @Override
    public Optional<Build> findBuild(ID branchId, Predicate<Build> buildPredicate, BuildSortDirection sortDirection) {
        // Gets the branch
        Branch branch = getBranch(branchId);
        // Build being found
        AtomicReference<Build> ref = new AtomicReference<>();
        // Loops over the builds
        structureRepository.builds(
                branch,
                build -> {
                    boolean ok = buildPredicate.test(build);
                    if (ok) {
                        ref.set(build);
                    }
                    return !ok; // Going on if no match
                },
                sortDirection
        );
        // Result
        return Optional.ofNullable(ref.get());
    }

    @Override
    public Optional<Build> getLastBuild(ID branchId) {
        return Optional.ofNullable(
                structureRepository.getLastBuildForBranch(
                        getBranch(branchId)
                )
        );
    }

    @Override
    public List<Build> buildSearch(ID projectId, BuildSearchForm form) {
        // Gets the project
        Project project = getProject(projectId);
        // Collects the builds for this project
        final List<Build> builds = new ArrayList<>();
        // Filter for the builds
        Predicate<Build> buildPredicate = build -> {
            // Build view
            Supplier<BuildView> buildViewSupplier = CachedSupplier.of(() -> getBuildView(build, false));
            // Branch name
            boolean accept;
            accept = !StringUtils.isNotBlank(form.getBranchName())
                    || Utils.safeRegexMatch(form.getBranchName(), build.getBranch().getName());
            // Build name
            if (accept && StringUtils.isNotBlank(form.getBuildName())) {
                if (form.isBuildExactMatch()) {
                    accept = StringUtils.equals(form.getBuildName(), build.getName());
                } else {
                    accept = Utils.safeRegexMatch(form.getBuildName(), build.getName());
                }
            }
            // Promotion name
            if (accept && StringUtils.isNotBlank(form.getPromotionName())) {
                BuildView buildView = buildViewSupplier.get();
                accept = buildView.getPromotionRuns().stream()
                        .anyMatch(run -> form.getPromotionName().equals(run.getPromotionLevel().getName()));
            }
            // Validation stamp name
            if (accept && StringUtils.isNotBlank(form.getValidationStampName())) {
                BuildView buildView = buildViewSupplier.get();
                accept = buildView.getValidationStampRunViews().stream()
                        .anyMatch(validationStampRunView -> validationStampRunView.hasValidationStamp(form.getValidationStampName(), ValidationRunStatusID.PASSED));
            }
            // Property & property value
            if (accept && StringUtils.isNotBlank(form.getProperty())) {
                accept = PropertyServiceHelper.hasProperty(
                        propertyService,
                        build,
                        form.getProperty(),
                        form.getPropertyValue());
            }
            // Linked from
            String linkedFrom = form.getLinkedFrom();
            if (accept && isNotBlank(linkedFrom)) {
                String projectName = StringUtils.substringBefore(linkedFrom, ":");
                String buildPattern = StringUtils.substringAfter(linkedFrom, ":");
                accept = isLinkedFrom(build, projectName, buildPattern);
            }
            // Linked to
            String linkedTo = form.getLinkedTo();
            if (accept && isNotBlank(linkedTo)) {
                String projectName = StringUtils.substringBefore(linkedTo, ":");
                String buildPattern = StringUtils.substringAfter(linkedTo, ":");
                accept = isLinkedTo(build, projectName, buildPattern);
            }
            // Accepting the build into the list?
            if (accept) {
                builds.add(build);
            }
            // Maximum count reached?
            return builds.size() < form.getMaximumCount();
        };
        // Query
        structureRepository.builds(project, buildPredicate);
        // OK
        return builds;
    }

    @Override
    public void addBuildLink(Build fromBuild, Build toBuild) {
        securityService.checkProjectFunction(fromBuild, BuildConfig.class);
        securityService.checkProjectFunction(toBuild, ProjectView.class);
        structureRepository.addBuildLink(fromBuild.getId(), toBuild.getId());
    }

    @Override
    public void deleteBuildLink(Build fromBuild, Build toBuild) {
        securityService.checkProjectFunction(fromBuild, BuildConfig.class);
        securityService.checkProjectFunction(toBuild, ProjectView.class);
        structureRepository.deleteBuildLink(fromBuild.getId(), toBuild.getId());
    }

    @Override
    public List<Build> getBuildLinksFrom(Build build) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.getBuildLinksFrom(build.getId()).stream()
                .filter(b -> securityService.isProjectFunctionGranted(b, ProjectView.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Build> getBuildLinksTo(Build build) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.getBuildLinksTo(build.getId()).stream()
                .filter(b -> securityService.isProjectFunctionGranted(b, ProjectView.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Build> searchBuildsLinkedTo(String projectName, String buildPattern) {
        return structureRepository.searchBuildsLinkedTo(projectName, buildPattern)
                .stream()
                .filter(b -> securityService.isProjectFunctionGranted(b, ProjectView.class))
                .collect(Collectors.toList());
    }

    @Override
    public void editBuildLinks(Build build, BuildLinkForm form) {
        securityService.checkProjectFunction(build, BuildConfig.class);
        // Gets the existing links, with authorisations
        Set<ID> authorisedExistingLinks = getBuildLinksFrom(build).stream()
                .map(Build::getId)
                .collect(Collectors.toSet());
        // Added links
        Set<ID> addedLinks = new HashSet<>();
        // Loops through the new links
        form.getLinks().forEach(item -> {
            // Gets the project if possible
            Project project = findProjectByName(item.getProject())
                    .orElseThrow(() -> new ProjectNotFoundException(item.getProject()));
            // Finds the build if possible (exact match - no regex)
            List<Build> builds = buildSearch(project.getId(), new BuildSearchForm()
                    .withMaximumCount(1)
                    .withBuildName(item.getBuild())
                    .withBuildExactMatch(true)
            );
            if (!builds.isEmpty()) {
                Build target = builds.get(0);
                // Adds the link
                addBuildLink(build, target);
                addedLinks.add(target.getId());
            } else {
                throw new BuildNotFoundException(item.getProject(), item.getBuild());
            }
        });
        // Deletes all authorised links which were not added again
        if (!form.isAddOnly()) {
            // Other links, not authorised to view, were not subject to edition and are not visible
            Set<ID> linksToDelete = new HashSet<>(authorisedExistingLinks);
            linksToDelete.removeAll(addedLinks);
            linksToDelete.forEach(id -> deleteBuildLink(
                    build,
                    getBuild(id)
            ));
        }
    }

    @Override
    public boolean isLinkedFrom(Build build, String project, String buildPattern) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.isLinkedFrom(build.getId(), project, buildPattern);
    }

    @Override
    public boolean isLinkedTo(Build build, String project, String buildPattern) {
        securityService.checkProjectFunction(build, ProjectView.class);
        return structureRepository.isLinkedTo(build.getId(), project, buildPattern);
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
        PromotionLevel newPromotionLevel = structureRepository.newPromotionLevel(
                promotionLevel.withSignature(securityService.getCurrentSignature())
        );
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
        // Repository
        structureRepository.setPromotionLevelImage(promotionLevelId, document);
        // Event
        eventPostService.post(eventFactory.imagePromotionLevel(promotionLevel));
    }

    @Override
    public void savePromotionLevel(PromotionLevel promotionLevel) {
        // Validation
        isEntityDefined(promotionLevel, "Promotion level must be defined");
        isEntityDefined(promotionLevel.getBranch(), "Branch must be defined");
        isEntityDefined(promotionLevel.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelEdit.class);
        // Repository
        structureRepository.savePromotionLevel(promotionLevel);
        // Event
        eventPostService.post(eventFactory.updatePromotionLevel(promotionLevel));
    }

    @Override
    public Ack deletePromotionLevel(ID promotionLevelId) {
        Validate.isTrue(promotionLevelId.isSet(), "Promotion level ID must be set");
        PromotionLevel promotionLevel = getPromotionLevel(promotionLevelId);
        securityService.checkProjectFunction(promotionLevel.projectId(), PromotionLevelDelete.class);
        eventPostService.post(eventFactory.deletePromotionLevel(promotionLevel));
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
        // Actual reordering
        structureRepository.reorderPromotionLevels(branchId, reordering);
        // Event
        eventPostService.post(eventFactory.reorderPromotionLevels(branch));
    }

    @Override
    public PromotionLevel newPromotionLevelFromPredefined(Branch branch, PredefinedPromotionLevel predefinedPromotionLevel) {
        PromotionLevel promotionLevel = newPromotionLevel(
                PromotionLevel.of(
                        branch,
                        NameDescription.nd(predefinedPromotionLevel.getName(), predefinedPromotionLevel.getDescription())
                )
        );

        // Makes sure the order is the same than for the predefined promotion levels
        List<PredefinedPromotionLevel> predefinedPromotionLevels = securityService.asAdmin(
                predefinedPromotionLevelService::getPredefinedPromotionLevels
        );
        List<Integer> sortedIds = getPromotionLevelListForBranch(branch.getId()).stream()
                .sorted((o1, o2) -> {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    // Looking for the order in the predefined list
                    int order1 = Iterables.indexOf(predefinedPromotionLevels, pred -> StringUtils.equals(pred.getName(), name1));
                    int order2 = Iterables.indexOf(predefinedPromotionLevels, pred -> StringUtils.equals(pred.getName(), name2));
                    // Comparing the orders
                    return (order1 - order2);
                })
                .map(Entity::id)
                .collect(Collectors.toList());
        reorderPromotionLevels(branch.getId(), new Reordering(sortedIds));

        // Image?
        if (predefinedPromotionLevel.getImage() != null && predefinedPromotionLevel.getImage()) {
            setPromotionLevelImage(
                    promotionLevel.getId(),
                    predefinedPromotionLevelService.getPredefinedPromotionLevelImage(predefinedPromotionLevel.getId())
            );
        }
        // OK
        return promotionLevel;
    }

    @Override
    public PromotionLevel getOrCreatePromotionLevel(Branch branch, Integer promotionLevelId, String promotionLevelName) {
        if (promotionLevelId != null) {
            return getPromotionLevel(ID.of(promotionLevelId));
        } else {
            Optional<PromotionLevel> oPromotionLevel = findPromotionLevelByName(
                    branch.getProject().getName(),
                    branch.getName(),
                    promotionLevelName
            );
            if (oPromotionLevel.isPresent()) {
                return oPromotionLevel.get();
            } else {
                List<Property<?>> properties = propertyService.getProperties(branch.getProject());
                for (Property<?> property : properties) {
                    PropertyType<?> type = property.getType();
                    if (type instanceof PromotionLevelPropertyType && !property.isEmpty()) {
                        oPromotionLevel = getPromotionLevelFromProperty(
                                property,
                                branch,
                                promotionLevelName
                        );
                        if (oPromotionLevel.isPresent()) {
                            return oPromotionLevel.get();
                        }
                    }
                }
                throw new PromotionLevelNotFoundException(
                        branch.getProject().getName(),
                        branch.getName(),
                        promotionLevelName
                );
            }
        }
    }

    protected <T> Optional<PromotionLevel> getPromotionLevelFromProperty(
            Property<T> property,
            Branch branch,
            String promotionLevelName) {
        PromotionLevelPropertyType<T> promotionLevelPropertyType = (PromotionLevelPropertyType<T>) property.getType();
        return promotionLevelPropertyType.getOrCreatePromotionLevel(
                property.getValue(),
                branch,
                promotionLevelName
        );
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
        return promotionRun;
    }

    @Override
    public List<PromotionRun> getPromotionRunsForBuild(ID buildId) {
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getPromotionRunsForBuild(build);
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
        ValidationStamp newValidationStamp = structureRepository.newValidationStamp(
                validationStamp.withSignature(securityService.getCurrentSignature())
        );
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
    public BranchStatusView getEarliestPromotionsAfterBuild(Build build) {
        return new BranchStatusView(
                build.getBranch(),
                decorationService.getDecorations(build.getBranch()),
                getLastBuild(build.getBranch().getId()).orElse(null),
                getPromotionLevelListForBranch(build.getBranch().getId()).stream()
                        .map(promotionLevel ->
                                new PromotionView(
                                        promotionLevel,
                                        getEarliestPromotionRunAfterBuild(promotionLevel, build).orElse(null)
                                )
                        )
                        .collect(Collectors.toList())
        );
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
    public BuildView getBuildView(Build build, boolean withDecorations) {
        BuildView view = BuildView.of(build)
                .withPromotionRuns(getLastPromotionRunsForBuild(build.getId()))
                .withValidationStampRunViews(getValidationStampRunViewsForBuild(build));
        if (withDecorations) {
            view = view.withDecorations(decorationService.getDecorations(build));
        }
        return view;
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
        // Event
        eventPostService.post(eventFactory.imageValidationStamp(validationStamp));
    }

    @Override
    public void saveValidationStamp(ValidationStamp validationStamp) {
        // Validation
        isEntityDefined(validationStamp, "Validation stamp must be defined");
        isEntityDefined(validationStamp.getBranch(), "Branch must be defined");
        isEntityDefined(validationStamp.getBranch().getProject(), "Project must be defined");
        // Security
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampEdit.class);
        // Repository
        structureRepository.saveValidationStamp(validationStamp);
        // Event
        eventPostService.post(eventFactory.updateValidationStamp(validationStamp));
    }

    @Override
    public Ack deleteValidationStamp(ID validationStampId) {
        Validate.isTrue(validationStampId.isSet(), "Validation stamp ID must be set");
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.projectId(), ValidationStampDelete.class);
        eventPostService.post(eventFactory.deleteValidationStamp(validationStamp));
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
        // Actual reordering
        structureRepository.reorderValidationStamps(branchId, reordering);
        // Event
        eventPostService.post(eventFactory.reorderValidationStamps(branch));
    }

    @Override
    public ValidationStamp getOrCreateValidationStamp(Branch branch, Integer validationStampId, String validationStampName) {
        if (validationStampId != null) {
            return getValidationStamp(ID.of(validationStampId));
        } else {
            Optional<ValidationStamp> oValidationStamp = findValidationStampByName(
                    branch.getProject().getName(),
                    branch.getName(),
                    validationStampName
            );
            if (oValidationStamp.isPresent()) {
                return oValidationStamp.get();
            } else {
                List<Property<?>> properties = propertyService.getProperties(branch.getProject());
                for (Property<?> property : properties) {
                    PropertyType<?> type = property.getType();
                    if (type instanceof ValidationStampPropertyType && !property.isEmpty()) {
                        oValidationStamp = getValidationStampFromProperty(
                                property,
                                branch,
                                validationStampName
                        );
                        if (oValidationStamp.isPresent()) {
                            return oValidationStamp.get();
                        }
                    }
                }
                throw new ValidationStampNotFoundException(
                        branch.getProject().getName(),
                        branch.getName(),
                        validationStampName
                );
            }
        }
    }

    @Override
    public Ack bulkUpdateValidationStamps(ID validationStampId) {
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings.class);
        // As admin
        securityService.asAdmin(() -> {
            ValidationStamp validationStamp = getValidationStamp(validationStampId);
            // Defining or replacing the predefined validation stamp
            Optional<PredefinedValidationStamp> o = predefinedValidationStampService.findPredefinedValidationStampByName(validationStamp.getName());
            if (o.isPresent()) {
                // Updating the predefined validation stamp description
                predefinedValidationStampService.savePredefinedValidationStamp(
                        o.get().withDescription(validationStamp.getDescription())
                );
                // Sets its image
                Document image = getValidationStampImage(validationStampId);
                predefinedValidationStampService.setPredefinedValidationStampImage(
                        o.get().getId(),
                        image
                );
            } else {
                // Creating the predefined validation stamp
                PredefinedValidationStamp predefinedValidationStamp = predefinedValidationStampService.newPredefinedValidationStamp(
                        PredefinedValidationStamp.of(
                                NameDescription.nd(
                                        validationStamp.getName(),
                                        validationStamp.getDescription()
                                )
                        )
                );
                // Sets its image
                Document image = getValidationStampImage(validationStampId);
                predefinedValidationStampService.setPredefinedValidationStampImage(
                        predefinedValidationStamp.getId(),
                        image
                );
            }
            // For all validation stamps
            structureRepository.bulkUpdateValidationStamps(validationStampId);
        });
        // OK
        return Ack.OK;
    }

    protected <T> Optional<ValidationStamp> getValidationStampFromProperty(
            Property<T> property,
            Branch branch,
            String validationStampName) {
        ValidationStampPropertyType<T> validationStampPropertyType = (ValidationStampPropertyType<T>) property.getType();
        return validationStampPropertyType.getOrCreateValidationStamp(
                property.getValue(),
                branch,
                validationStampName
        );
    }

    @Override
    public ValidationStamp newValidationStampFromPredefined(Branch branch, PredefinedValidationStamp predefinedValidationStamp) {
        ValidationStamp validationStamp = newValidationStamp(
                ValidationStamp.of(
                        branch,
                        NameDescription.nd(predefinedValidationStamp.getName(), predefinedValidationStamp.getDescription())
                )
        );
        // Image?
        if (predefinedValidationStamp.getImage() != null && predefinedValidationStamp.getImage()) {
            setValidationStampImage(
                    validationStamp.getId(),
                    predefinedValidationStampService.getPredefinedValidationStampImage(predefinedValidationStamp.getId())
            );
        }
        // OK
        return validationStamp;
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
        ValidationRun newValidationRun = structureRepository.newValidationRun(validationRun, validationRunStatusService::getValidationRunStatus);
        // Event
        eventPostService.post(eventFactory.newValidationRun(newValidationRun));
        // OK
        return newValidationRun;
    }

    @Override
    public ValidationRun getValidationRun(ID validationRunId) {
        ValidationRun validationRun = structureRepository.getValidationRun(validationRunId, validationRunStatusService::getValidationRunStatus);
        securityService.checkProjectFunction(validationRun.getBuild().getBranch().getProject().id(), ProjectView.class);
        return validationRun;
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuild(ID buildId) {
        Build build = getBuild(buildId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getValidationRunsForBuild(build, validationRunStatusService::getValidationRunStatus);
    }

    @Override
    public List<ValidationRun> getValidationRunsForBuildAndValidationStamp(ID buildId, ID validationStampId) {
        Build build = getBuild(buildId);
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(build.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getValidationRunsForBuildAndValidationStamp(build, validationStamp, validationRunStatusService::getValidationRunStatus);
    }

    @Override
    public List<ValidationRun> getValidationRunsForValidationStamp(ID validationStampId, int offset, int count) {
        ValidationStamp validationStamp = getValidationStamp(validationStampId);
        securityService.checkProjectFunction(validationStamp.getBranch().getProject().id(), ProjectView.class);
        return structureRepository.getValidationRunsForValidationStamp(validationStamp, offset, count, validationRunStatusService::getValidationRunStatus);
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
                .filter(p ->
                        securityService.isGlobalFunctionGranted(ProjectList.class) ||
                                securityService.isProjectFunctionGranted(p.id(), ProjectView.class));
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

}
