package net.nemerosa.ontrack.boot.ui;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.BranchNotTemplateDefinitionException;
import net.nemerosa.ontrack.model.exceptions.BranchNotTemplateInstanceException;
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

import static net.nemerosa.ontrack.boot.ui.UIUtils.requestParametersToJson;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class BranchController extends AbstractResourceController {

    private final StructureService structureService;
    private final BranchTemplateService branchTemplateService;
    private final TemplateSynchronisationService templateSynchronisationService;
    private final CopyService copyService;
    private final BuildFilterService buildFilterService;
    private final ExtensionManager extensionManager;
    private final SecurityService securityService;
    private final BranchFavouriteService branchFavouriteService;

    @Autowired
    public BranchController(
            StructureService structureService,
            BranchTemplateService branchTemplateService,
            TemplateSynchronisationService templateSynchronisationService,
            CopyService copyService,
            BuildFilterService buildFilterService,
            ExtensionManager extensionManager,
            SecurityService securityService,
            BranchFavouriteService branchFavouriteService) {
        this.structureService = structureService;
        this.branchTemplateService = branchTemplateService;
        this.templateSynchronisationService = templateSynchronisationService;
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

    /**
     * Gets a form to make this branch a template definition.
     */
    @RequestMapping(value = "branches/{branchId}/template/definition", method = RequestMethod.GET)
    public Form getTemplateDefinition(@PathVariable ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        Optional<TemplateDefinition> templateDefinition = branchTemplateService.getTemplateDefinition(branchId);
        return Form.create()
                .with(
                        MultiForm.of(
                                "parameters",
                                Form.create()
                                        .with(
                                                Text.of("name").label("Name").help("Parameter name")
                                        )
                                        .with(
                                                Text.of("description").optional().label("Description").help("Parameter description")
                                        )
                                        .with(
                                                Text.of("expression")
                                                        .label("Expression")
                                                        .optional()
                                                        .help(
                                                                "Those expressions are defined for the synchronisation between " +
                                                                        "template definitions and template instances. They bind " +
                                                                        "a parameter name and a source name to an actual parameter " +
                                                                        "value. " +
                                                                        "A template expression is a string that contains " +
                                                                        "references to the source name using the ${...} construct " +
                                                                        "where the content is a Groovy expression where the " +
                                                                        "`sourceName` variable is bound to the source name.")
                                        )
                        )
                                .label("Parameters")
                                .help("List of parameters that define the template")
                                .value(templateDefinition
                                        .map(TemplateDefinition::getParameters)
                                        .orElse(Collections.emptyList()))
                )
                .with(
                        ServiceConfigurator.of("synchronisationSourceConfig")
                                .label("Sync. source")
                                .optional()
                                .help("Source names when synchronising")
                                .sources(
                                        templateSynchronisationService.getSynchronisationSources().stream()
                                                .filter(source -> source.isApplicable(branch))
                                                .map(
                                                        source -> new ServiceConfigurationSource(
                                                                source.getId(),
                                                                source.getName(),
                                                                source.getForm(branch)
                                                        )
                                                )
                                                .collect(Collectors.toList())
                                )
                                .value(
                                        templateDefinition
                                                .map(TemplateDefinition::getSynchronisationSourceConfig)
                                                .orElse(null)
                                )
                )
                .with(
                        Int.of("interval")
                                .label("Interval")
                                .help("Interval between each synchronisation in minutes. If set to zero, " +
                                        "no automated synchronisation is performed and it must be done " +
                                        "manually.")
                                .min(0)
                                .value(templateDefinition.map(TemplateDefinition::getInterval).orElse(0))
                )
                .with(
                        Selection.of("absencePolicy")
                                .label("Absence policy")
                                .help("Defines what to do with a branch template instance when the corresponding " +
                                        "name is not defined any longer.")
                                .items(Arrays.stream(TemplateSynchronisationAbsencePolicy.values())
                                        .map(Describable::toDescription)
                                        .collect(Collectors.toList()))
                                .itemId("id")
                                .itemName("name")
                                .value(
                                        templateDefinition
                                                .map(td -> td.getAbsencePolicy().getId())
                                                .orElse(TemplateSynchronisationAbsencePolicy.DISABLE.getId())
                                )
                )
                ;
    }

    /**
     * Sets this branch as a template definition, or updates the definition.
     */
    @RequestMapping(value = "branches/{branchId}/template/definition", method = RequestMethod.PUT)
    public Branch setTemplateDefinition(@PathVariable ID branchId, @RequestBody @Valid TemplateDefinition templateDefinition) {
        return branchTemplateService.setTemplateDefinition(branchId, templateDefinition);
    }

    /**
     * Sync. this template definition by creating and updating linked template instances.
     */
    @RequestMapping(value = "branches/{branchId}/template/sync", method = RequestMethod.POST)
    public BranchTemplateSyncResults syncTemplateDefinition(@PathVariable ID branchId) {
        return branchTemplateService.sync(branchId);
    }

    /**
     * Sync. this template instance against its template definition
     */
    @RequestMapping(value = "branches/{branchId}/sync", method = RequestMethod.POST)
    public Ack syncTemplateInstance(@PathVariable ID branchId) {
        return branchTemplateService.syncInstance(branchId);
    }

    /**
     * Gets the form to create a template instance using a name.
     */
    @RequestMapping(value = "branches/{branchId}/template", method = RequestMethod.GET)
    public Form singleTemplateInstanceForm(@PathVariable ID branchId) {
        // Gets the template definition for this branch
        Optional<TemplateDefinition> templateDefinition = branchTemplateService.getTemplateDefinition(branchId);
        if (!templateDefinition.isPresent()) {
            throw new BranchNotTemplateDefinitionException(branchId);
        }
        // Creates a form with the source name and all needed parameters
        Form form = Form.create().with(
                Form.defaultNameField()
                        .label("Source name")
                        .help("Name used to create the branch from.")
        );
        form.append(templateDefinition.get().getForm());
        // OK
        return form;
    }

    /**
     * Creates a branch template instance for one name.
     * <p>
     * <ul>
     * <li>If the target branch does not exist, creates it.</li>
     * <li>If the target branch exists:
     * <ul>
     * <li>If it is linked to the same definition, updates it.</li>
     * <li>If it is linked to another definition, this is an error.</li>
     * <li>If it is a normal branch, this is an error.</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param branchId ID of the branch template definition
     * @param request  Name to use when creating the branch
     * @return Created or updated branch
     */
    @RequestMapping(value = "branches/{branchId}/template", method = RequestMethod.PUT)
    public Branch createTemplateInstance(@PathVariable ID branchId, @RequestBody @Valid BranchTemplateInstanceSingleRequest request) {
        return branchTemplateService.createTemplateInstance(branchId, request);
    }

    /**
     * Gets the template instance for a branch
     */
    @RequestMapping(value = "branches/{branchId}/template/instance", method = RequestMethod.GET)
    public TemplateInstance getTemplateInstance(@PathVariable ID branchId) {
        return branchTemplateService.getTemplateInstance(branchId)
                .orElseThrow(() -> new BranchNotTemplateInstanceException(branchId));
    }

    /**
     * Disconnects the branch from any template definition, if any.
     */
    @RequestMapping(value = "branches/{branchId}/template/instance", method = RequestMethod.DELETE)
    public Branch disconnectTemplateInstance(@PathVariable ID branchId) {
        return branchTemplateService.disconnectTemplateInstance(branchId);
    }

    /**
     * Gets the form to connect a branch to a template.
     */
    @RequestMapping(value = "branches/{branchId}/template/connect", method = RequestMethod.GET)
    public Form connectTemplateInstance(@PathVariable ID branchId) {
        // Loads the branch
        Branch branch = structureService.getBranch(branchId);
        // List of templates
        Collection<LoadedBranchTemplateDefinition> templateDefinitions = branchTemplateService.getTemplateDefinitions(branch.getProject());
        // List of sync form per template
        return Form.create()
                .with(
                        ServiceConfigurator.of("connectionRequest")
                                .label("Template")
                                .help("Template to connect to")
                                .sources(
                                        templateDefinitions.stream()
                                                .map(lbtd -> new ServiceConfigurationSource(
                                                        lbtd.getBranch().getId().toString(),
                                                        lbtd.getBranch().getName(),
                                                        lbtd.getForm()
                                                ))
                                                .collect(Collectors.toList())
                                )
                );
    }

    /**
     * Tries to connect an existing branch to a template
     *
     * @param branchId Branch to connect
     */
    @RequestMapping(value = "branches/{branchId}/template/connect", method = RequestMethod.POST)
    public Branch connectTemplateInstance(@PathVariable ID branchId, @RequestBody BranchTemplateInstanceConnectRequest request) {
        return branchTemplateService.connectTemplateInstance(branchId, request);
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
