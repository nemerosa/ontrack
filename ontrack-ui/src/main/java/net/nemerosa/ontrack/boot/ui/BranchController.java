package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.form.*;
import net.nemerosa.ontrack.model.security.BranchCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.ui.support.UIUtils.requestParametersToJson;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/rest/structure")
public class BranchController extends AbstractResourceController {

    private final StructureService structureService;
    private final CopyService copyService;
    private final BuildFilterService buildFilterService;
    private final ExtensionManager extensionManager;
    private final SecurityService securityService;
    private final BranchFavouriteService branchFavouriteService;

    @Autowired
    public BranchController(
            StructureService structureService,
            CopyService copyService,
            BuildFilterService buildFilterService,
            ExtensionManager extensionManager,
            SecurityService securityService,
            BranchFavouriteService branchFavouriteService) {
        this.structureService = structureService;
        this.copyService = copyService;
        this.buildFilterService = buildFilterService;
        this.extensionManager = extensionManager;
        this.securityService = securityService;
        this.branchFavouriteService = branchFavouriteService;
    }

    @RequestMapping(value = "projects/{projectId}/branches", method = RequestMethod.GET)
    public Resources<Branch> getBranchListForProject(@PathVariable ID projectId) {
        return Resources.of(
                structureService.getBranchesForProject(projectId),
                uri(on(BranchController.class).getBranchListForProject(projectId))
        )
                // Create
                .with(
                        Link.CREATE,
                        uri(on(BranchController.class).newBranch(projectId, null)),
                        securityService.isProjectFunctionGranted(projectId.getValue(), BranchCreate.class)
                )
                ;
    }

    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.GET)
    public Form newBranchForm(@PathVariable ID projectId) {
        // Checks the project exists
        structureService.getProject(projectId);
        // Returns the form
        return Branch.form();
    }

    @RequestMapping(value = "projects/{projectId}/branches/create", method = RequestMethod.POST)
    public Branch newBranch(@PathVariable ID projectId, @RequestBody @Valid NameDescriptionState nameDescription) {
        // Gets the project
        Project project = structureService.getProject(projectId);
        // Creates a new branch instance
        Branch branch = Branch.of(project, nameDescription);
        // Saves it into the repository
        branch = structureService.newBranch(branch);
        // OK
        return branch;
    }

    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.GET)
    public Branch getBranch(@PathVariable ID branchId) {
        return structureService.getBranch(branchId);
    }

    @RequestMapping(value = "branches/{branchId}", method = RequestMethod.DELETE)
    public Ack deleteBranch(@PathVariable ID branchId) {
        return structureService.deleteBranch(branchId);
    }

    @RequestMapping(value = "branches/{branchId}/update", method = RequestMethod.GET)
    public Form getUpdateForm(@PathVariable ID branchId) {
        // Loads the branch and gets a form
        return structureService.getBranch(branchId).toForm();
    }

    @RequestMapping(value = "branches/{branchId}/update", method = RequestMethod.PUT)
    public Branch updateBranch(@PathVariable ID branchId, @RequestBody @Valid NameDescriptionState form) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId).update(form);
        // Saves the branch
        structureService.saveBranch(branch);
        // OK
        return branch;
    }

    @RequestMapping(value = "branches/{branchId}/enable", method = RequestMethod.PUT)
    public Branch enableBranch(@PathVariable ID branchId) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId);
        // Saves the branch
        return structureService.enableBranch(branch);
    }

    @RequestMapping(value = "branches/{branchId}/disable", method = RequestMethod.PUT)
    public Branch disableBranch(@PathVariable ID branchId) {
        // Loads and updates branch
        Branch branch = structureService.getBranch(branchId).withDisabled(true);
        // Disables the branch
        return structureService.disableBranch(branch);
    }

    @RequestMapping(value = "branches/{branchId}/status", method = RequestMethod.GET)
    public BranchStatusView getBranchStatusView(@PathVariable ID branchId) {
        return structureService.getBranchStatusView(structureService.getBranch(branchId));
    }

    @RequestMapping(value = "branches/{branchId}/view", method = RequestMethod.GET)
    public BranchBuildView buildView(@PathVariable ID branchId) {
        return buildViewWithFilter(
                branchId,
                buildFilterService.defaultFilterProviderData()
        );

    }

    @RequestMapping(value = "branches/{branchId}/view/{filterType:.*}", method = RequestMethod.GET)
    public <T> BranchBuildView buildViewWithFilter(@PathVariable ID branchId, @PathVariable String filterType, WebRequest request) {
        JsonNode jsonParameters = requestParametersToJson(request);
        // Gets the filter provider
        BuildFilterProviderData<T> buildFilterProvider = buildFilterService.getBuildFilterProviderData(filterType, jsonParameters);
        // Gets the build view
        return buildViewWithFilter(
                branchId,
                buildFilterProvider
        );
    }

    /**
     * Gets the form to copy a source branch into this branch
     */
    @RequestMapping(value = "branches/{branchId}/copy", method = RequestMethod.GET)
    public Form copy(@PathVariable ID branchId) {
        return Form.create()
                .with(
                        Selection.of("sourceBranch")
                                .label("Source branch")
                                .help("Branch to copy configuration from")
                                // All branches for all projects
                                .items(structureService.getProjectList().stream()
                                        .flatMap(project -> structureService.getBranchesForProject(project.getId()).stream())
                                        // Keeps only the different branches
                                        .filter(branch -> !branchId.equals(branch.getId()))
                                        .collect(Collectors.toList()))
                )
                .with(
                        Replacements.of("replacements")
                                .label("Replacements")
                )
                ;
    }

    /**
     * Copies the configuration from a branch into this one.
     */
    @RequestMapping(value = "branches/{branchId}/copy", method = RequestMethod.PUT)
    public Branch copy(@PathVariable ID branchId, @RequestBody BranchCopyRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the copy
        return copyService.copy(branch, request);
    }

    /**
     * Gets the form for a bulk update of the branch
     */
    @RequestMapping(value = "branches/{branchId}/update/bulk", method = RequestMethod.GET)
    public Form bulkUpdate(@SuppressWarnings("UnusedParameters") @PathVariable ID branchId) {
        return Form.create()
                .with(
                        Replacements.of("replacements")
                                .label("Replacements")
                )
                ;
    }

    /**
     * Bulk update for a branch.
     */
    @RequestMapping(value = "branches/{branchId}/update/bulk", method = RequestMethod.PUT)
    public Branch bulkUpdate(@PathVariable ID branchId, @RequestBody BranchBulkUpdateRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the update
        return copyService.update(branch, request);
    }

    /**
     * Gets the form to clone this branch into another branch
     */
    @RequestMapping(value = "branches/{branchId}/clone", method = RequestMethod.GET)
    public Form clone(@SuppressWarnings("UnusedParameters") @PathVariable ID branchId) {
        return Form.create()
                .with(
                        Text.of("name")
                                .label("Target branch")
                                .help("Name of the branch to create")
                )
                .with(
                        Replacements.of("replacements")
                                .label("Replacements")
                )
                ;
    }

    /**
     * Clones this branch into another one.
     */
    @RequestMapping(value = "branches/{branchId}/clone", method = RequestMethod.POST)
    public Branch clone(@PathVariable ID branchId, @RequestBody BranchCloneRequest request) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Performs the clone
        return copyService.cloneBranch(branch, request);
    }

    private <T> BranchBuildView buildViewWithFilter(ID branchId,
                                                    BuildFilterProviderData<T> buildFilterProviderData) {
        // Gets the branch
        Branch branch = structureService.getBranch(branchId);
        // Gets the list of builds
        List<Build> builds = buildFilterProviderData.filterBranchBuilds(branch);
        // Gets the list of build diff actions
        List<Action> buildDiffActions = extensionManager.getExtensions(BuildDiffExtension.class)
                .stream()
                .filter(extension -> extension.apply(branch.getProject()))
                .map(this::resolveExtensionAction)
                .collect(Collectors.toList());
        // Gets the views for each build
        return new BranchBuildView(
                builds.stream()
                        .map(build -> structureService.getBuildView(build, true))
                        .collect(Collectors.toList()),
                buildDiffActions
        );
    }

    @RequestMapping(value = "branches/{branchId}/favourite", method = RequestMethod.PUT)
    public Branch favouriteBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        branchFavouriteService.setBranchFavourite(branch, true);
        return branch;
    }

    @RequestMapping(value = "branches/{branchId}/unfavourite", method = RequestMethod.PUT)
    public Branch unfavouriteBranch(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        branchFavouriteService.setBranchFavourite(branch, false);
        return branch;
    }

}
