package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.BuildDiffExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class BuildController extends AbstractResourceController {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final ExtensionManager extensionManager;

    @Autowired
    public BuildController(StructureService structureService, PropertyService propertyService, SecurityService securityService, ExtensionManager extensionManager) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.extensionManager = extensionManager;
    }

    @RequestMapping(value = "project/{projectId}/builds", method = RequestMethod.GET)
    public Resource<Form> buildSearchForm(@PathVariable ID projectId) {
        return Resource.of(
                createBuildSearchForm(),
                uri(on(getClass()).buildSearchForm(projectId))
        ).with("_search", uri(on(getClass()).buildSearch(projectId, null)));
    }

    private Form createBuildSearchForm() {
        // List of properties for a build
        List<PropertyTypeDescriptor> properties = propertyService.getPropertyTypes().stream()
                .filter(
                        type -> type.getSupportedEntityTypes().contains(ProjectEntityType.BUILD)
                )
                .map(PropertyTypeDescriptor::of)
                .collect(Collectors.toList());
        // Form
        return Form.create()
                .with(
                        Int.of("maximumCount")
                                .label("Maximum number")
                                .help("Maximum number of builds to return.")
                                .min(1)
                                .value(10)
                )
                .with(
                        Text.of("branchName")
                                .label("Branch name")
                                .help("Regular expression for the branch name")
                                .optional()
                )
                .with(
                        Text.of("buildName")
                                .label("Build name")
                                .help("Regular expression for the build name")
                                .optional()
                )
                .with(
                        Text.of("promotionName")
                                .label("Promotion name")
                                .help("Collects only builds which are promoted to this promotion level.")
                                .optional()
                )
                .with(
                        Text.of("validationStampName")
                                .label("Validation stamp name")
                                .help("Collects only builds which have `passed` this validation stamp.")
                                .optional()
                )

                .with(
                        Selection.of("property")
                                .label("With property")
                                .items(properties)
                                .itemId("typeName")
                                .optional()
                )
                .with(
                        Text.of("propertyValue")
                                .label("... with value")
                                .length(40)
                                .optional()
                )
                ;
    }

    /**
     * Build search
     */
    @RequestMapping(value = "project/{projectId}/builds/search", method = RequestMethod.GET)
    public Resources<BuildView> buildSearch(@PathVariable ID projectId, @Valid BuildSearchForm form) {
        return Resources.of(
                structureService.buildSearch(projectId, form).stream()
                        .map(build -> structureService.getBuildView(build, true))
                        .collect(Collectors.toList()),
                uri(on(getClass()).buildSearch(projectId, form)))
                .forView(BuildView.class)
                ;
    }

    /**
     * List of diff actions
     */
    @RequestMapping(value = "project/{projectId}/builds/diff", method = RequestMethod.GET)
    public Resources<Action> buildDiffActions(@PathVariable ID projectId) {
        return Resources.of(
                extensionManager.getExtensions(BuildDiffExtension.class)
                        .stream()
                        .filter(extension -> extension.apply(structureService.getProject(projectId)))
                        .map(this::resolveExtensionAction)
                        .collect(Collectors.toList()),
                uri(on(getClass()).buildDiffActions(projectId))
        );
    }

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.GET)
    public Form newBuildForm(@PathVariable ID branchId) {
        // Checks the branch does exist
        structureService.getBranch(branchId);
        // Returns the form
        return Build.form();
    }

    @RequestMapping(value = "branches/{branchId}/builds/create", method = RequestMethod.POST)
    public Build newBuild(@PathVariable ID branchId, @RequestBody @Valid BuildRequest request) {
        // Gets the holding branch
        Branch branch = structureService.getBranch(branchId);
        // Build signature
        Signature signature = securityService.getCurrentSignature();
        // Creates a new build
        Build build = Build.of(branch, request.asNameDescription(), signature);
        // Saves it into the repository
        build = structureService.newBuild(build);
        // Saves the properties
        for (PropertyCreationRequest propertyCreationRequest : request.getProperties()) {
            propertyService.editProperty(
                    build,
                    propertyCreationRequest.getPropertyTypeName(),
                    propertyCreationRequest.getPropertyData()
            );
        }
        // OK
        return build;
    }

    @RequestMapping(value = "builds/{buildId}/update", method = RequestMethod.GET)
    public Form updateBuildForm(@PathVariable ID buildId) {
        return structureService.getBuild(buildId).asForm();
    }

    @RequestMapping(value = "builds/{buildId}/update", method = RequestMethod.PUT)
    public Build updateBuild(@PathVariable ID buildId, @RequestBody @Valid NameDescription nameDescription) {
        // Gets from the repository
        Build build = structureService.getBuild(buildId);
        // Updates
        build = build.update(nameDescription);
        // Saves in repository
        structureService.saveBuild(build);
        // As resource
        return build;
    }

    /**
     * Update form for the build signature.
     */
    @RequestMapping(value = "builds/{buildId}/signature", method = RequestMethod.GET)
    public Form updateBuildSignatureForm(@PathVariable ID buildId) {
        return SignatureRequest.of(
                structureService.getBuild(buildId).getSignature()
        ).asForm();
    }

    /**
     * Update the build signature
     */
    @RequestMapping(value = "builds/{buildId}/signature", method = RequestMethod.PUT)
    public Build updateBuildSignature(@PathVariable ID buildId, @RequestBody SignatureRequest request) {
        // Gets from the repository
        Build build = structureService.getBuild(buildId);
        // Updates
        build = build.withSignature(
                request.getSignature(build.getSignature())
        );
        // Saves in repository
        structureService.saveBuild(build);
        // As resource
        return build;
    }

    @RequestMapping(value = "builds/{buildId}", method = RequestMethod.DELETE)
    public Ack deleteBuild(@PathVariable ID buildId) {
        return structureService.deleteBuild(buildId);
    }

    @RequestMapping(value = "builds/{buildId}", method = RequestMethod.GET)
    public Build getBuild(@PathVariable ID buildId) {
        return structureService.getBuild(buildId);
    }

    /**
     * Gets the previous build
     */
    @RequestMapping(value = "builds/{buildId}/previous", method = RequestMethod.GET)
    public ResponseEntity<Build> getPreviousBuild(@PathVariable ID buildId) {
        return structureService.getPreviousBuild(buildId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }

    /**
     * Gets the next build
     */
    @RequestMapping(value = "builds/{buildId}/next", method = RequestMethod.GET)
    public ResponseEntity<Build> getNextBuild(@PathVariable ID buildId) {
        return structureService.getNextBuild(buildId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }


}
