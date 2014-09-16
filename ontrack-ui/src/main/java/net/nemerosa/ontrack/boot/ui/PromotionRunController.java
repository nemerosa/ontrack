package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.DateTime;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Time;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @RequestMapping(value = "builds/{buildId}/promotionRun/last", method = RequestMethod.GET)
    public Resources<PromotionRun> getLastPromotionRuns(@PathVariable ID buildId) {
        return Resources.of(
                structureService.getLastPromotionRunsForBuild(buildId),
                uri(on(getClass()).getLastPromotionRuns(buildId))
        ).forView(Build.class);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/create", method = RequestMethod.GET)
    public Form newPromotionRunForm(@PathVariable ID buildId) {
        Build build = structureService.getBuild(buildId);
        return Form.create()
                .with(
                        Selection.of("promotionLevel")
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
    public PromotionRun newPromotionRun(@PathVariable ID buildId, @RequestBody @Valid PromotionRunRequest promotionRunRequest) {
        // Gets the build
        Build build = structureService.getBuild(buildId);
        // Gets the promotion level
        PromotionLevel promotionLevel = structureService.getPromotionLevel(ID.of(promotionRunRequest.getPromotionLevel()));
        // Promotion run to create
        PromotionRun promotionRun = PromotionRun.of(
                build,
                promotionLevel,
                securityService.getCurrentSignature().withTime(promotionRunRequest.getDateTime()),
                promotionRunRequest.getDescription()
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
        return promotionRun;
    }

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.GET)
    public PromotionRun getPromotionRun(@PathVariable ID promotionRunId) {
        return structureService.getPromotionRun(promotionRunId);
    }

}
