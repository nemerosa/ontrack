package net.nemerosa.ontrack.boot.ui;

import jakarta.validation.Valid;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rest/structure")
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
    public List<PromotionRun> getPromotionRuns(@PathVariable ID buildId) {
        return structureService.getPromotionRunsForBuild(buildId);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/last", method = RequestMethod.GET)
    public List<PromotionRun> getLastPromotionRuns(@PathVariable ID buildId) {
        return structureService.getLastPromotionRunsForBuild(buildId);
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/{promotionLevelId}", method = RequestMethod.GET)
    public List<PromotionRun> getPromotionRunsForBuildAndPromotionLevel(@PathVariable ID buildId, @PathVariable ID promotionLevelId) {
        return structureService.getPromotionRunsForBuildAndPromotionLevel(
                structureService.getBuild(buildId),
                structureService.getPromotionLevel(promotionLevelId)
        );
    }

    @RequestMapping(value = "builds/{buildId}/promotionRun/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PromotionRun> newPromotionRun(@PathVariable ID buildId, @RequestBody @Valid PromotionRunRequest promotionRunRequest) {
        // Gets the build
        Build build = structureService.getBuild(buildId);
        // Gets the promotion level
        PromotionLevel promotionLevel = getPromotionLevel(
                build.getBranch(),
                promotionRunRequest.getPromotionLevelId(),
                promotionRunRequest.getPromotionLevelName()
        ).getBody();
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
        return ResponseEntity.ok(promotionRun);
    }

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.GET)
    public ResponseEntity<PromotionRun> getPromotionRun(@PathVariable ID promotionRunId) {
        return ResponseEntity.ok(structureService.getPromotionRun(promotionRunId));
    }

    @RequestMapping(value = "promotionRuns/{promotionRunId}", method = RequestMethod.DELETE)
    public ResponseEntity<Ack> deletePromotionRun(@PathVariable ID promotionRunId) {
        return ResponseEntity.ok(structureService.deletePromotionRun(promotionRunId));
    }

    protected ResponseEntity<PromotionLevel> getPromotionLevel(Branch branch, Integer promotionLevelId, String promotionLevelName) {
        return ResponseEntity.ok(structureService.getOrCreatePromotionLevel(branch, promotionLevelId, promotionLevelName));
    }

}
