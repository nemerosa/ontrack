package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class ValidationRunController extends AbstractResourceController {

    private final StructureService structureService;
    private final ValidationRunStatusService validationRunStatusService;
    private final SecurityService securityService;

    @Autowired
    public ValidationRunController(StructureService structureService, ValidationRunStatusService validationRunStatusService, SecurityService securityService) {
        this.structureService = structureService;
        this.validationRunStatusService = validationRunStatusService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns/view", method = RequestMethod.GET)
    public ResourceCollection<ValidationStampRunView> getValidationStampRunViews(@PathVariable ID buildId) {
        // Build
        Build build = structureService.getBuild(buildId);
        // Gets all validation stamps
        List<ValidationStamp> stamps = structureService.getValidationStampListForBranch(build.getBranch().getId());
        // Gets all runs for this build
        List<ValidationRun> runs = structureService.getValidationRunsForBuild(buildId);
        // Converts into a view
        URI uri = uri(on(getClass()).getValidationStampRunViews(buildId));
        return ResourceCollection.of(
                stamps.stream()
                        .map(stamp ->
                                        new ValidationStampRunView(
                                                stamp,
                                                runs.stream()
                                                        .filter(run -> run.getValidationStamp().id() == stamp.id())
                                                        .collect(Collectors.toList())
                                        )
                        )
                        .map(view -> Resource.of(view, uri)
                                        .with(Link.IMAGE_LINK, uri(on(ValidationStampController.class).getValidationStampImage_(view.getValidationStamp().getId())))
                        )
                        .collect(Collectors.toList()),
                uri
        ).forView(ValidationStampRunView.class);
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns", method = RequestMethod.GET)
    public ResourceCollection<ValidationRun> getValidationRuns(@PathVariable ID buildId) {
        return ResourceCollection.of(
                structureService.getValidationRunsForBuild(buildId)
                        .stream()
                        .map(this::toValidationRunResource)
                        .collect(Collectors.toList()),
                uri(on(BuildController.class).getLastPromotionRuns(buildId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns/create", method = RequestMethod.GET)
    public Form newValidationRunForm(@PathVariable ID buildId) {
        Build build = structureService.getBuild(buildId);
        return Form.create()
                .with(
                        Selection.of("validationStamp")
                                .label("Validation stamp")
                                .items(structureService.getValidationStampListForBranch(build.getBranch().getId()))
                )
                .with(
                        Selection.of("validationRunStatusId")
                                .label("Status")
                                .items(validationRunStatusService.getValidationRunStatusRoots())
                )
                .description();
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<ValidationRun> newValidationRun(@PathVariable ID buildId, @RequestBody ValidationRunRequest validationRunRequest) {
        // Gets the build
        Build build = structureService.getBuild(buildId);
        // Gets the validation stamp
        ValidationStamp validationStamp = structureService.getValidationStamp(ID.of(validationRunRequest.getValidationStamp()));
        // Gets the validation run status
        ValidationRunStatusID validationRunStatusID = validationRunStatusService.getValidationRunStatus(validationRunRequest.getValidationRunStatusId());
        // Validation run to create
        ValidationRun validationRun = ValidationRun.of(
                build,
                validationStamp,
                securityService.getCurrentSignature(),
                validationRunStatusID,
                validationRunRequest.getDescription()
        );
        // Creation
        validationRun = structureService.newValidationRun(validationRun);
        // OK
        return toValidationRunResource(validationRun);
    }

    @RequestMapping(value = "validationRuns/{validationRunId}", method = RequestMethod.GET)
    public Resource<ValidationRun> getValidationRun(@PathVariable ID validationRunId) {
        return toValidationRunResource(structureService.getValidationRun(validationRunId));
    }

    // Validation run status

    @RequestMapping(value = "validationRuns/{validationRunId}/status/change", method = RequestMethod.GET)
    public Form getValidationRunStatusChangeForm(@PathVariable ID validationRunId) {
        ValidationRun validationRun = structureService.getValidationRun(validationRunId);
        return Form.create()
                .with(
                        Selection.of("validationRunStatusId")
                                .label("Status")
                                .items(
                                        validationRunStatusService.getNextValidationRunStatusList(validationRun.getLastStatus().getStatusID().getId())
                                )
                )
                .description()
                ;
    }

    @RequestMapping(value = "validationRuns/{validationRunId}/status/change", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<ValidationRun> validationRunStatusChange(@PathVariable ID validationRunId, @RequestBody ValidationRunStatusChangeRequest request) {
        // Gets the current run
        ValidationRun run = structureService.getValidationRun(validationRunId);
        // Gets the new validation run status
        ValidationRunStatus runStatus = ValidationRunStatus.of(
                securityService.getCurrentSignature(),
                validationRunStatusService.getValidationRunStatus(request.getValidationRunStatusId()),
                request.getDescription()
        );
        // Updates the validation run
        ValidationRun updatedRun = structureService.newValidationRunStatus(run, runStatus);
        // OK
        return toValidationRunResource(updatedRun);
    }

    // Resource assemblers

    private Resource<ValidationRun> toValidationRunResource(ValidationRun validationRun) {
        return Resource.of(
                validationRun,
                uri(on(getClass()).getValidationRun(validationRun.getId()))
        ).with(
                Link.IMAGE_LINK, uri(on(ValidationStampController.class).getValidationStampImage_(validationRun.getValidationStamp().getId()))
        ).with(
                "validationStampLink", uri(on(ValidationStampController.class).getValidationStamp(validationRun.getValidationStamp().getId()))
        ).with(
                "validationRunStatusChange",
                uri(on(ValidationRunController.class).getValidationRunStatusChangeForm(validationRun.getId())),
                // Only if transition possible
                securityService.isProjectFunctionGranted(
                        validationRun.getBuild().getBranch().getProject().id(),
                        ValidationRunStatusChange.class
                ) && !validationRun.getLastStatus().getStatusID().getFollowingStatuses().isEmpty()
        );
    }
}
