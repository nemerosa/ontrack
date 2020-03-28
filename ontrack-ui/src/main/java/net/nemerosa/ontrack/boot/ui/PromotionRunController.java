package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.form.DateTime;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Objects;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class PromotionRunController extends AbstractResourceController {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    @Autowired
    public PromotionRunController(StructureService structureService, PropertyService propertyService, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun", method = RequestMethod.GET)
    public Resources<PromotionRun> getPromotionRuns(@PathVariable ID buildId) {
        return Resources.of(
                structureService.getPromotionRunsForBuild(buildId),
                uri(on(getClass()).getPromotionRuns(buildId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/last", method = RequestMethod.GET)
    public Resources<PromotionRun> getLastPromotionRuns(@PathVariable ID buildId) {
        return Resources.of(
                structureService.getLastPromotionRunsForBuild(buildId),
                uri(on(getClass()).getLastPromotionRuns(buildId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/{promotionLevelId}", method = RequestMethod.GET)
    public Resources<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(@PathVariable ID buildId, @PathVariable ID promotionLevelId) {
        return Resources.of(
                structureService.getPromotionRunsForBuildAndPromotionLevel(
                        structureService.getBuild(buildId),
                        structureService.getPromotionLevel(promotionLevelId)
                ),
                uri(on(getClass()).getPromotionRunsForBuildAndPromotionLevel(buildId, promotionLevelId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/create", method = RequestMethod.GET)
    public Form newPromotionRunForm(@PathVariable ID buildId) {
        Build build = structureService.getBuild(buildId);
        return Form.create()
                .with(
                        Selection.of("promotionLevelId")
                                .label("Promotion level")
                                .items(structureService.getPromotionLevelListForBranch(build.getBranch().getId()))
                )
                .with(
                        DateTime.of("dateTime")
                                .label("Date/time")
                                .value(Time.now())
                                .minuteStep(15)
                )
                .description();
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Resource<PromotionRun> newPromotionRun(@PathVariable ID buildId, @RequestBody @Valid PromotionRunRequest promotionRunRequest) {
        // Gets the build
        Build build = structureService.getBuild(buildId);
        // Gets the promotion level
        PromotionLevel promotionLevel = getPromotionLevel(
                build.getBranch(),
                promotionRunRequest.getPromotionLevelId(),
                promotionRunRequest.getPromotionLevelName());
        // Promotion run to create
        PromotionRun promotionRun = PromotionRun.of(
                build,
                promotionLevel,
                securityService.getCurrentSignature().withTime(promotionRunRequest.getDateTime()),
                Objects.toString(promotionRunRequest.getDescription(), "")
        );
        // Creation
        promotionRun = structureService.newPromotionRun(promotionRun);
        // Saves the properties
        for (PropertyCreationRequest propertyCreationRequest : promotionRunRequest.getProperties()) {
            propertyService.editProperty(
                    promotionRun,
                    propertyCreationRequest.getPropertyTypeName(),
                    propertyCreationRequest.getPropertyData()
            );
        }
        // OK
        return asResource(promotionRun);
    }

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.GET)
    public Resource<PromotionRun> getPromotionRun(@PathVariable ID promotionRunId) {
        return asResource(structureService.getPromotionRun(promotionRunId));
    }

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.DELETE)
    public Ack deletePromotionRun(@PathVariable ID promotionRunId) {
        return structureService.deletePromotionRun(promotionRunId);
    }

    protected PromotionLevel getPromotionLevel(Branch branch, Integer promotionLevelId, String promotionLevelName) {
        return structureService.getOrCreatePromotionLevel(branch, promotionLevelId, promotionLevelName);
    }

    private Resource<PromotionRun> asResource(PromotionRun promotionRun) {
        return Resource.of(promotionRun, uri(on(PromotionRunController.class).getPromotionRun(promotionRun.getId())));
    }

}
