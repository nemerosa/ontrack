package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.BranchEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CopyServiceImpl implements CopyService {

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final BuildFilterService buildFilterService;

    @Autowired
    public CopyServiceImpl(StructureService structureService, PropertyService propertyService, SecurityService securityService, BuildFilterService buildFilterService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.buildFilterService = buildFilterService;
    }

    @Override
    public Branch copy(Branch targetBranch, BranchCopyRequest request) {
        // Gets the source branch
        Branch sourceBranch = structureService.getBranch(request.getSourceBranchId());
        // If same branch, rejects
        if (sourceBranch.id() == targetBranch.id()) {
            throw new IllegalArgumentException("Cannot copy the branch into itself.");
        }
        // Checks the rights on the target branch
        securityService.checkProjectFunction(targetBranch, BranchEdit.class);
        // Now, we can work in a secure context
        securityService.asAdmin(() -> doCopy(sourceBranch, targetBranch, request));
        return targetBranch;
    }

    protected void doCopy(Branch sourceBranch, Branch targetBranch, BranchCopyRequest request) {
        // Branch properties
        doCopyProperties(sourceBranch, targetBranch, request.getPropertyReplacements());
        // Promotion level and properties
        doCopyPromotionLevels(sourceBranch, targetBranch, request);
        // Validation stamps and properties
        doCopyValidationStamps(sourceBranch, targetBranch, request);
        // User filters
        doCopyUserBuildFilters(sourceBranch, targetBranch);
    }

    protected void doCopyUserBuildFilters(Branch sourceBranch, Branch targetBranch) {
        buildFilterService.copyToBranch(sourceBranch.getId(), targetBranch.getId());
    }

    protected void doCopyPromotionLevels(Branch sourceBranch, Branch targetBranch, BranchCopyRequest request) {
        List<PromotionLevel> sourcePromotionLevels = structureService.getPromotionLevelListForBranch(sourceBranch.getId());
        for (PromotionLevel sourcePromotionLevel : sourcePromotionLevels) {
            Optional<PromotionLevel> targetPromotionLevelOpt = structureService.findPromotionLevelByName(targetBranch.getProject().getName(), targetBranch.getName(), sourcePromotionLevel.getName());
            if (!targetPromotionLevelOpt.isPresent()) {
                // Copy of the promotion level
                PromotionLevel targetPromotionLevel = structureService.newPromotionLevel(
                        PromotionLevel.of(
                                targetBranch,
                                NameDescription.nd(
                                        sourcePromotionLevel.getName(),
                                        applyReplacements(sourcePromotionLevel.getDescription(), request.getPromotionLevelReplacements())
                                )
                        )
                );
                // Copy of the image
                Document image = structureService.getPromotionLevelImage(sourcePromotionLevel.getId());
                if (image != null) {
                    structureService.setPromotionLevelImage(targetPromotionLevel.getId(), image);
                }
                // Copy of properties
                doCopyProperties(sourcePromotionLevel, targetPromotionLevel, request.getPromotionLevelReplacements());
            }
        }
    }

    protected void doCopyProperties(ProjectEntity source, ProjectEntity target, List<Replacement> replacements) {
        List<Property<?>> properties = propertyService.getProperties(source);
        for (Property<?> property : properties) {
            doCopyProperty(property, target, replacements);
        }
    }

    protected void doCopyValidationStamps(Branch sourceBranch, Branch targetBranch, BranchCopyRequest request) {
        List<ValidationStamp> sourceValidationStamps = structureService.getValidationStampListForBranch(sourceBranch.getId());
        for (ValidationStamp sourceValidationStamp : sourceValidationStamps) {
            Optional<ValidationStamp> targetValidationStampOpt = structureService.findValidationStampByName(targetBranch.getProject().getName(), targetBranch.getName(), sourceValidationStamp.getName());
            if (!targetValidationStampOpt.isPresent()) {
                // Copy of the validation stamp
                ValidationStamp targetValidationStamp = structureService.newValidationStamp(
                        ValidationStamp.of(
                                targetBranch,
                                NameDescription.nd(
                                        sourceValidationStamp.getName(),
                                        applyReplacements(sourceValidationStamp.getDescription(), request.getValidationStampReplacements())
                                )
                        )
                );
                // Copy of the image
                Document image = structureService.getValidationStampImage(sourceValidationStamp.getId());
                if (image != null) {
                    structureService.setValidationStampImage(targetValidationStamp.getId(), image);
                }
                // Copy of properties
                doCopyProperties(sourceValidationStamp, targetValidationStamp, request.getValidationStampReplacements());
            }
        }
    }

    protected <T> void doCopyProperty(Property<T> property, ProjectEntity targetEntity, List<Replacement> replacements) {
        if (!property.isEmpty() && property.getType().canEdit(targetEntity, securityService)) {
            // Property value replacement
            T data = property.getType().replaceValue(property.getValue(), s -> applyReplacements(s, replacements));
            // Property data
            JsonNode jsonData = property.getType().forStorage(data);
            // Creates the property
            propertyService.editProperty(
                    targetEntity,
                    property.getType().getTypeName(),
                    jsonData
            );
        }
    }

    protected static String applyReplacements(final String value, List<Replacement> replacements) {
        String transformedValue = value;
        for (Replacement replacement : replacements) {
            transformedValue = replacement.replace(transformedValue);
        }
        return transformedValue;
    }

}
