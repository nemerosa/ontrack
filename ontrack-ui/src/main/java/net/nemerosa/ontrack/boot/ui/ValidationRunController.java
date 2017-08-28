package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Pagination;
import net.nemerosa.ontrack.ui.resource.PaginationCountException;
import net.nemerosa.ontrack.ui.resource.PaginationOffsetException;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class ValidationRunController extends AbstractResourceController {

    private final StructureService structureService;
    private final ValidationRunStatusService validationRunStatusService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public ValidationRunController(StructureService structureService, ValidationRunStatusService validationRunStatusService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.validationRunStatusService = validationRunStatusService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns/view", method = RequestMethod.GET)
    public Resources<ValidationStampRunView> getValidationStampRunViews(@PathVariable ID buildId) {
        // Build
        Build build = structureService.getBuild(buildId);
        // Gets the views
        List<ValidationStampRunView> views = structureService.getValidationStampRunViewsForBuild(build);
        // Converts into a view
        URI uri = uri(on(getClass()).getValidationStampRunViews(buildId));
        return Resources.of(
                views,
                uri
        ).forView(ValidationStampRunView.class);
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns", method = RequestMethod.GET)
    public Resources<ValidationRun> getValidationRuns(@PathVariable ID buildId) {
        return Resources.of(
                structureService.getValidationRunsForBuild(buildId),
                uri(on(ValidationRunController.class).getValidationRuns(buildId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/validationRuns/create", method = RequestMethod.GET)
    public Form newValidationRunForm(@PathVariable ID buildId) {
        Build build = structureService.getBuild(buildId);
        return Form.create()
                .with(
                        Selection.of("validationStampId")
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
    public ValidationRun newValidationRun(@PathVariable ID buildId, @RequestBody ValidationRunRequest validationRunRequest) {
        // Gets the build
        Build build = structureService.getBuild(buildId);
        // Gets the validation stamp
        ValidationStamp validationStamp = getValidationStamp(build.getBranch(), validationRunRequest.getValidationStampId(), validationRunRequest.getActualValidationStampName());
        // Gets the validation run status
        ValidationRunStatusID validationRunStatusID = validationRunStatusService.getValidationRunStatus(validationRunRequest.getValidationRunStatusId());
        // Validation run to create
        ValidationRun validationRun = ValidationRun.of(
                build,
                validationStamp,
                0,
                securityService.getCurrentSignature(),
                validationRunStatusID,
                validationRunRequest.getDescription()
        );
        // Creation
        validationRun = structureService.newValidationRun(validationRun);
        // Saves the properties
        for (PropertyCreationRequest propertyCreationRequest : validationRunRequest.getProperties()) {
            propertyService.editProperty(
                    validationRun,
                    propertyCreationRequest.getPropertyTypeName(),
                    propertyCreationRequest.getPropertyData()
            );
        }
        // OK
        return validationRun;
    }

    protected ValidationStamp getValidationStamp(Branch branch, Integer validationStampId, String validationStampName) {
        return structureService.getOrCreateValidationStamp(branch, validationStampId, validationStampName);
    }

    @RequestMapping(value = "validationRuns/{validationRunId}", method = RequestMethod.GET)
    public ValidationRun getValidationRun(@PathVariable ID validationRunId) {
        return structureService.getValidationRun(validationRunId);
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
    public ValidationRun validationRunStatusChange(@PathVariable ID validationRunId, @RequestBody ValidationRunStatusChangeRequest request) {
        // Gets the current run
        ValidationRun run = structureService.getValidationRun(validationRunId);
        // Gets the new validation run status
        ValidationRunStatus runStatus = ValidationRunStatus.of(
                securityService.getCurrentSignature(),
                validationRunStatusService.getValidationRunStatus(request.getValidationRunStatusId()),
                request.getDescription()
        );
        // Updates the validation run
        return structureService.newValidationRunStatus(run, runStatus);
    }

    /**
     * List of validation runs for a validation stamp
     */
    @RequestMapping(value = "validationStamps/{validationStampId}/validationRuns", method = RequestMethod.GET)
    public Resources<ValidationRun> getValidationRunsForValidationStamp(
            @PathVariable ID validationStampId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        // Gets ALL the runs
        List<ValidationRun> runs = structureService.getValidationRunsForValidationStamp(validationStampId, 0, Integer.MAX_VALUE);
        // Total number of runs
        int total = runs.size();
        // Checks the offset and count
        if (offset < 0) {
            throw new PaginationOffsetException(offset);
        } else if (offset > 0 && offset >= total) {
            throw new PaginationOffsetException(offset);
        } else if (count <= 0) {
            throw new PaginationCountException(count);
        }
        // Prepares the resources
        Resources<ValidationRun> resources = Resources.of(
                runs.subList(offset, Math.min(offset + count, runs.size())),
                uri(on(ValidationRunController.class).getValidationRunsForValidationStamp(
                        validationStampId,
                        offset,
                        count
                ))
        );
        // Pagination information
        Pagination pagination = Pagination.of(offset, count, total);
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(ValidationRunController.class).getValidationRunsForValidationStamp(
                            validationStampId,
                            Math.max(0, offset - count),
                            count
                    ))
            );
        }
        // Next page
        if (offset + count < total) {
            pagination = pagination.withNext(
                    uri(on(ValidationRunController.class).getValidationRunsForValidationStamp(
                            validationStampId,
                            offset + count,
                            count
                    ))
            );
        }
        // OK
        return resources.withPagination(pagination).forView(ValidationStampRunView.class);
    }

}
