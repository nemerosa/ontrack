package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.properties.AutoPromotionLevelProperty;
import net.nemerosa.ontrack.boot.properties.AutoPromotionLevelPropertyType;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException;
import net.nemerosa.ontrack.model.form.DateTime;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/structure")
public class PromotionRunController extends AbstractResourceController {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final PredefinedPromotionLevelService predefinedPromotionLevelService;

    @Autowired
    public PromotionRunController(StructureService structureService, PropertyService propertyService, SecurityService securityService, PredefinedPromotionLevelService predefinedPromotionLevelService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.predefinedPromotionLevelService = predefinedPromotionLevelService;
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
        PromotionLevel promotionLevel = getPromotionLevel(
                build.getBranch(),
                promotionRunRequest.getPromotionLevelId(),
                promotionRunRequest.getPromotionLevelName());
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

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.DELETE)
    public Ack deletePromotionRun(@PathVariable ID promotionRunId) {
        return structureService.deletePromotionRun(promotionRunId);
    }

    protected PromotionLevel getPromotionLevel(Branch branch, Integer promotionLevelId, String promotionLevelName) {
        if (promotionLevelId != null) {
            return structureService.getPromotionLevel(ID.of(promotionLevelId));
        } else {
            Optional<PromotionLevel> oPromotionLevel = structureService.findPromotionLevelByName(
                    branch.getProject().getName(),
                    branch.getName(),
                    promotionLevelName
            );
            if (oPromotionLevel.isPresent()) {
                return oPromotionLevel.get();
            } else {
                Optional<AutoPromotionLevelProperty> oAutoPromotionLevelProperty = propertyService.getProperty(branch.getProject(), AutoPromotionLevelPropertyType.class).option();
                // Checks if the project allows for auto creation of promotion levels
                if (oAutoPromotionLevelProperty.isPresent() && oAutoPromotionLevelProperty.get().isAutoCreate()) {
                    Optional<PredefinedPromotionLevel> oPredefinedPromotionLevel = predefinedPromotionLevelService.findPredefinedPromotionLevelByName(promotionLevelName);
                    if (oPredefinedPromotionLevel.isPresent()) {
                        // Creates the promotion level
                        return securityService.asAdmin(() -> createPromotionLevel(branch, oPredefinedPromotionLevel.get()));
                    } else {
                        throw new PromotionLevelNotFoundException(
                                branch.getProject().getName(),
                                branch.getName(),
                                promotionLevelName
                        );
                    }
                } else {
                    throw new PromotionLevelNotFoundException(
                            branch.getProject().getName(),
                            branch.getName(),
                            promotionLevelName
                    );
                }
            }
        }
    }

    private PromotionLevel createPromotionLevel(Branch branch, PredefinedPromotionLevel predefinedPromotionLevel) {
        PromotionLevel promotionLevel = structureService.newPromotionLevel(
                PromotionLevel.of(
                        branch,
                        NameDescription.nd(predefinedPromotionLevel.getName(), predefinedPromotionLevel.getDescription())
                )
        );
        // Image?
        if (predefinedPromotionLevel.getImage() != null && predefinedPromotionLevel.getImage()) {
            structureService.setPromotionLevelImage(
                    promotionLevel.getId(),
                    predefinedPromotionLevelService.getPredefinedPromotionLevelImage(predefinedPromotionLevel.getId())
            );
        }
        // OK
        return promotionLevel;
    }
}
